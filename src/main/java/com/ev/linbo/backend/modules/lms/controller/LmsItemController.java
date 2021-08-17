package com.ev.linbo.backend.modules.lms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ev.linbo.backend.common.api.CommonPage;
import com.ev.linbo.backend.common.api.CommonResult;
import com.ev.linbo.backend.modules.lms.dto.LmsAllocateParam;
import com.ev.linbo.backend.modules.lms.dto.LmsItemCountParam;
import com.ev.linbo.backend.modules.lms.dto.LmsItemQueryParam;
import com.ev.linbo.backend.modules.lms.mapper.LmsOrderMapper;
import com.ev.linbo.backend.modules.lms.model.LmsItem;
import com.ev.linbo.backend.modules.lms.model.LmsOrder;
import com.ev.linbo.backend.modules.lms.service.LmsItemService;
import com.ev.linbo.backend.security.annotation.OperateLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 货物表 前端控制器
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
@Controller
@Api(tags = "LmsItemController", description = "货物管理")
@RequestMapping("/item")
public class LmsItemController {

    private final LmsItemService lmsItemService;

    private final LmsOrderMapper lmsOrderMapper;

    public LmsItemController(LmsItemService lmsItemService, LmsOrderMapper lmsOrderMapper) {
        this.lmsItemService = lmsItemService;
        this.lmsOrderMapper = lmsOrderMapper;
    }

    @ApiOperation("添加包裹")
    @OperateLog(module="包裹管理-包裹新增",operateType="OPERATE_ADD",operation="包裹新增功能")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody LmsItem item) throws InterruptedException {
        boolean success = lmsItemService.create(item);
        List<LmsOrder> orders = lmsOrderMapper.getPreciseOrderList(item.getDeliverySn(), item.getUserSn());
        if (orders.size() > 0) {
            lmsItemService.allocateOrder(item.getId(), orders.get(0).getId());
        }
        if (success) {
            return CommonResult.success(item.getId());
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改货物")
    @OperateLog(module="包裹管理-包裹修改",operateType="OPERATE_MOD",operation="包裹修改功能")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update( @RequestBody LmsItem item, @PathVariable Long id) {
        item.setId(id);
        boolean success = lmsItemService.updateById(item);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改包裹状态")
    @OperateLog(module="包裹管理-包裹状态运转",operateType="OPERATE_FWD",operation="包裹状态运转功能")
    @RequestMapping(value = "/updateStatus/{orderAction}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateItemStatus(@RequestBody LmsItem item, @PathVariable String orderAction) {
        String result = lmsItemService.updateItemStatus(item, orderAction);
        if (result.equals("成功")) {
            return CommonResult.success(null);
        }
        return CommonResult.failed(result);
    }

    @ApiOperation("通过订单修改包裹状态")
    @OperateLog(module="包裹管理-包裹状态运转",operateType="OPERATE_FWD_ORD",operation="包裹状态运转功能")
    @RequestMapping(value = "/updateStatusByOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateItemStatusByOrder(@RequestBody LmsOrder order) {
        List<LmsItem> items = lmsItemService.getItemListByOrder(order.getId());
        for (LmsItem item : items) {
            lmsItemService.updateItemStatus(item, order.getOrderAction());
        }
        return CommonResult.success(null);
    }

    @ApiOperation("批量删除货物")
    @OperateLog(module="包裹管理-删除包裹",operateType="OPERATE_DEL",operation="包裹删除功能")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        boolean success = lmsItemService.delete(ids);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("根据条件分页获取货物列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<CommonPage<LmsItem>> list(@RequestBody LmsItemQueryParam lmsItemQueryParam) {
        Page<LmsItem> itemList = lmsItemService.list(lmsItemQueryParam);
        return CommonResult.success(CommonPage.restPage(itemList));
    }

    @ApiOperation("查询货物是否存在")
    @RequestMapping(value = "/checkIfExist", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> listPrecise(@RequestParam(value = "deliverySn", required = false) String deliverySn,
                                                  @RequestParam(value = "userSn", required = false) String userSn,
                                                  @RequestParam(value = "location", required = false) String location) {
        Boolean isExist = lmsItemService.checkIfExist(deliverySn, userSn, location);
        return CommonResult.success(isExist);
    }

    @ApiOperation("获取指定货物的所有订单")
    @RequestMapping(value = "/order/{itemId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<LmsOrder>> getOrderList(@PathVariable Long itemId) {
        List<LmsOrder> orderList = lmsItemService.getOrderList(itemId);
        return CommonResult.success(orderList);
    }

    @ApiOperation("给货物分配订单")
    @RequestMapping(value = "/allocateOrder/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult allocateOrder(@RequestBody LmsAllocateParam lmsAllocateParam) {
        boolean result = lmsItemService.allocateOrder(lmsAllocateParam.getItemId(), lmsAllocateParam.getOrderId());
        if (result) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取货物统计数据")
    @RequestMapping(value = "/itemCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Long> itemCount(@RequestBody LmsItemCountParam lmsCountParam) {
        String dateString = "";
        if (!ObjectUtils.isEmpty(lmsCountParam.getDayOffset())) {
            dateString = LocalDate.now().minusDays(lmsCountParam.getDayOffset()).toString();
        }
        Long result = lmsItemService.fetchItemCount(lmsCountParam.getLocation(), dateString, lmsCountParam.getStatuses(),
                lmsCountParam.getUserSn());
        return CommonResult.success(result);
    }

    @ApiOperation("获取货物统计数据")
    @RequestMapping(value = "/modifyStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> modifyStatus(@RequestParam(value = "ids") List<Long> ids,
                                        @RequestParam(value = "newStatus", required = false) String newStatus) {
        Boolean result = lmsItemService.modifyItemStatus(ids, newStatus);
        return CommonResult.success(result);
    }

    @ApiOperation("通过订单刷新包裹状态")
    @RequestMapping(value = "/refreshItemStatusByOrder/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshItemStatusByOrder(@PathVariable Long orderId,
                                                 @RequestParam(value = "orderAction") String orderAction) {
        List<LmsItem> items = lmsItemService.getItemListByOrder(orderId);
        for (LmsItem item : items) {
            lmsItemService.refreshItemStatus(item, orderAction);
        }
        return CommonResult.success(null);
    }

}

