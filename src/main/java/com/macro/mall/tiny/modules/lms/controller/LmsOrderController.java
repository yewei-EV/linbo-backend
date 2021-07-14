package com.macro.mall.tiny.modules.lms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
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
@Api(tags = "LmsOrderController", description = "财务管理")
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
        order.setId(id);
        boolean success = lmsOrderService.updateById(order);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("用户修改订单")
    @RequestMapping(value = "/updateAction/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateByUser(@PathVariable Long id, @RequestParam(value = "orderAction") String orderAction,
                                     @RequestParam(value = "destination") String destination,
                                     @RequestParam(value = "attachment") String attachment) {
        boolean success = lmsOrderService.updateByUser(id, orderAction, destination, attachment);
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
        if (pageSize > 20) {
            return CommonResult.failed("Invalid page size!");
        }
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

    @ApiOperation("获取货物统计数据")
    @RequestMapping(value = "/orderCount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Long> orderCount(@RequestParam(value = "statusRange", required = false) String statusRange,
                                        @RequestParam(value = "userSn", required = false) String userSn) {
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
        Long result = lmsOrderService.fetchOrderCount(statusStart, statusEnd, userSn);
        return CommonResult.success(result);
    }
}

