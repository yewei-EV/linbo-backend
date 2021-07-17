package com.macro.mall.tiny.modules.lms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.lms.dto.LmsAllocateParam;
import com.macro.mall.tiny.modules.lms.dto.LmsItemQueryParam;
import com.macro.mall.tiny.modules.lms.mapper.LmsOrderMapper;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.service.LmsItemService;
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

    @ApiOperation("添加货物")
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
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody LmsItem item) {
        item.setId(id);
        boolean success = lmsItemService.updateById(item);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改包裹状态")
    @RequestMapping(value = "/updateStatus/{orderAction}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateItemStatus(@PathVariable String orderAction, @RequestBody LmsItem item) {
        String result = lmsItemService.updateItemStatus(item, orderAction);
        if (result.equals("成功")) {
            return CommonResult.success(null);
        }
        return CommonResult.failed(result);
    }

    @ApiOperation("通过订单修改包裹状态")
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

    @ApiOperation("根据条件分页获取精准货物列表")
    @RequestMapping(value = "/listPrecise", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<LmsItem>> listPrecise(@RequestParam(value = "deliverySn", required = false) String deliverySn,
                                                  @RequestParam(value = "userSn", required = false) String userSn,
                                                  @RequestParam(value = "location", required = false) String location,
                                                  @RequestParam(value = "note", required = false) String note,
                                                  @RequestParam(value = "createTime", required = false) String createTime,
                                                  @RequestParam(value = "sku", required = false) String sku,
                                                  @RequestParam(value = "size", required = false) String size,
                                                  @RequestParam(value = "itemStatus", required = false) Integer itemStatus,
                                                  @RequestParam(value = "positionInfo", required = false) String positionInfo,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<LmsItem> itemList = lmsItemService.listPrecise(deliverySn, userSn, location, note, createTime, sku, size,
                itemStatus, positionInfo, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(itemList));
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
    @RequestMapping(value = "/itemCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Long> itemCount(@RequestParam(value = "dayOffset", required = false) Integer dayOffset,
                                        @RequestParam(value = "location", required = false) String location,
                                        @RequestParam(value = "statusRange", required = false) String statusRange,
                                        @RequestParam(value = "userSn", required = false) String userSn) {
        String dateString = "";
        if (!ObjectUtils.isEmpty(dayOffset)) {
            dateString = LocalDate.now().minusDays(dayOffset).toString();
        }
        String statusStart = "";
        String statusEnd = "";
        String[] strings = statusRange.split(",");
        if (strings.length > 1) {
            statusStart = strings[0];
            statusEnd = strings[1];
        } else {
            statusStart = strings[0];
            statusEnd = strings[0];
        }
        Long result = lmsItemService.fetchItemCount(location, dateString, statusStart, statusEnd, userSn);
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

}

