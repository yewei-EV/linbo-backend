package com.macro.mall.tiny.modules.lms.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.model.LmsOrderItemRelation;
import com.macro.mall.tiny.modules.lms.service.LmsItemService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderItemRelationService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
@Controller
@Api(tags = "LmsOrderController", description = "订单管理")
@RequestMapping("/order")
public class LmsOrderController {

    private final LmsOrderService lmsOrderService;

    public LmsOrderController(LmsOrderService lmsOrderService) {
        this.lmsOrderService = lmsOrderService;
    }

    @ApiOperation("添加订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody LmsOrder order) {
        boolean success = lmsOrderService.create(order);
        if (success) {
            return CommonResult.success(order.getId());
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改订单")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody LmsOrder order) {
        //更新order对应的item的status
        LmsOrder currentOrder = lmsOrderService.getById(id);
        if (!currentOrder.getOrderStatus().equals(order.getOrderStatus())) {
            lmsOrderService.refreshItemsStatusByOrder(id, order);
        }
        order.setId(id);
        boolean success = lmsOrderService.updateById(order);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        boolean success = lmsOrderService.delete(ids);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取所有订单")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<LmsOrder>> listAll() {
        List<LmsOrder> orderList = lmsOrderService.list();
        return CommonResult.success(orderList);
    }

    @ApiOperation("根据条件分页获取订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<LmsOrder>> list(@RequestParam(value = "id", required = false) Long id,
                                                  @RequestParam(value = "orderAction", required = false) String orderAction,
                                                  @RequestParam(value = "deliverySn", required = false) String deliverySn,
                                                  @RequestParam(value = "userSn", required = false) String userSn,
                                                  @RequestParam(value = "destination", required = false) String destination,
                                                  @RequestParam(value = "note", required = false) String note,
                                                  @RequestParam(value = "createTime", required = false) String createTime,
                                                  @RequestParam(value = "orderStatus", required = false) Integer orderStatus,
                                                  @RequestParam(value = "paymentStatus", required = false) Integer paymentStatus,
                                                  @RequestParam(value = "paymentTime", required = false) String paymentTime,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<LmsOrder> orderList = lmsOrderService.list(id, orderAction, deliverySn, userSn, destination, note,
                createTime, orderStatus, paymentStatus, paymentTime, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(orderList));
    }

    @ApiOperation("获取销售额统计数据")
    @RequestMapping(value = "/orderPriceCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Float> priceCount(@RequestParam(value = "dayOffset", required = false) Integer dayOffset,
                                        @RequestParam(value = "location", required = false) String location) {
        LocalDate date = LocalDate.now().minusDays(dayOffset);
        Float result = lmsOrderService.fetchOrderPriceCount(location, date.toString());
        return CommonResult.success(result);
    }

}

