package com.macro.mall.tiny.modules.lms.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.lms.dto.LmsAllocateParam;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.service.LmsItemService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    private final LmsOrderService lmsOrderService;

    public LmsItemController(LmsItemService lmsItemService, LmsOrderService lmsOrderService) {
        this.lmsItemService = lmsItemService;
        this.lmsOrderService = lmsOrderService;
    }

    @ApiOperation("添加货物")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody LmsItem item) {
        boolean success = lmsItemService.create(item);
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

    @ApiOperation("获取所有货物")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<LmsItem>> listAll() {
        List<LmsItem> itemList = lmsItemService.list();
        return CommonResult.success(itemList);
    }

    @ApiOperation("根据条件分页获取货物列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<LmsItem>> list(@RequestParam(value = "deliverySn", required = false) String deliverySn,
                                                  @RequestParam(value = "userSn", required = false) String userSn,
                                                  @RequestParam(value = "location", required = false) String location,
                                                  @RequestParam(value = "note", required = false) String note,
                                                  @RequestParam(value = "createTime", required = false) String createTime,
                                                  @RequestParam(value = "sku", required = false) String sku,
                                                  @RequestParam(value = "size", required = false) String size,
                                                  @RequestParam(value = "status", required = false) Integer status,
                                                  @RequestParam(value = "positionInfo", required = false) String positionInfo,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<LmsItem> itemList = lmsItemService.list(deliverySn, userSn, location, note, createTime, sku, size,
                status, positionInfo, pageSize, pageNum);
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
                                                  @RequestParam(value = "status", required = false) Integer status,
                                                  @RequestParam(value = "positionInfo", required = false) String positionInfo,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<LmsItem> itemList = lmsItemService.listPrecise(deliverySn, userSn, location, note, createTime, sku, size,
                status, positionInfo, pageSize, pageNum);
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
        lmsOrderService.refreshItemsStatusByOrder( lmsAllocateParam.getOrderId());
        if (result) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

}

