package com.ev.linbo.backend.modules.lms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ev.linbo.backend.common.api.CommonPage;
import com.ev.linbo.backend.common.api.CommonResult;
import com.ev.linbo.backend.modules.lms.dto.LmsOrderCountParam;
import com.ev.linbo.backend.modules.lms.mapper.LmsOrderMapper;
import com.ev.linbo.backend.modules.lms.model.LmsOrder;
import com.ev.linbo.backend.modules.lms.service.LmsOrderService;
import com.ev.linbo.backend.security.annotation.OperateLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
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

    private final LmsOrderMapper lmsOrderMapper;

    public LmsOrderController(LmsOrderService lmsOrderService, LmsOrderMapper lmsOrderMapper) {
        this.lmsOrderService = lmsOrderService;
        this.lmsOrderMapper = lmsOrderMapper;
    }

    @ApiOperation("添加订单")
    @OperateLog(module="订单管理-订单新增",operateType="OPERATE_ADD",operation="订单新增功能")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody LmsOrder order) {
        List<LmsOrder> orders = lmsOrderMapper.getPreciseOrderList(order.getDeliverySn(), order.getUserSn());
        if (orders.size() <= 0) {
            boolean success = lmsOrderService.create(order);
            if (success) {
                return CommonResult.success(order.getId());
            }
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改订单")
    @OperateLog(module="订单管理-订单修改",operateType="OPERATE_MOD",operation="订单修改功能")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@RequestBody LmsOrder order, @PathVariable Long id) {
        order.setOrderStatus(lmsOrderService.refreshOrderStatus(order));
        order.setId(id);
        order.setUpdateTime(new Date());
        boolean success = lmsOrderService.updateById(order);
        if (order.getOrderAction().equals("-1") && order.getDeliverySn().split(",").length > 1) {
            lmsOrderService.separateOrders(order);
        }
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("用户修改订单")
    @OperateLog(module="订单管理-订单用户修改",operateType="OPERATE_MOD_USER",operation="订单用户修改功能")
    @RequestMapping(value = "/updateAction/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateByUser(@PathVariable Long id, @RequestParam(value = "orderAction") String orderAction,
                                     @RequestParam(value = "destination") String destination,
                                     @RequestParam(value = "attachment") String attachment,
                                     @RequestParam(value = "storageDays") Integer storageDays,
                                     @RequestParam(value = "storageLocation") String storageLocation,
                                     @RequestParam(value = "overtimeDate") String overtimeDate,
                                     @RequestParam(value = "labelNumber")  String labelNumber,
                                     @RequestParam(value = "userRemark")  String userRemark,
                                     @RequestParam(value = "chinaSize")  String chinaSize,
                                     @RequestParam(value = "isFollowPrice") Boolean isFollowPrice) {
        boolean success = lmsOrderService.updateByUser(id, orderAction, destination, attachment, storageDays,
                storageLocation, overtimeDate, labelNumber, userRemark, chinaSize, isFollowPrice);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除订单")
    @OperateLog(module="订单管理-删除订单",operateType="OPERATE_DEL",operation="订单删除功能")
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
                                                   @RequestParam(value = "location", required = false) String location,
                                                   @RequestParam(value = "createTime", required = false) String createTime,
                                                   @RequestParam(value = "orderStatus", required = false) Integer orderStatus,
                                                   @RequestParam(value = "paymentStatus", required = false) Integer paymentStatus,
                                                   @RequestParam(value = "paymentTime", required = false) String paymentTime,
                                                   @RequestParam(value = "newDeliverySn", required = false) String newDeliverySn,
                                                   @RequestParam(value = "updateTime", required = false) String updateTime,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<LmsOrder> orderList = lmsOrderService.list(id, orderAction, deliverySn, userSn, destination, note, location,
                createTime, orderStatus, paymentStatus, paymentTime, newDeliverySn, updateTime, pageSize, pageNum);
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
    @RequestMapping(value = "/orderCount", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Long> orderCount(@RequestBody LmsOrderCountParam lmsCountParam) {
        String dateString = "";
        if (!ObjectUtils.isEmpty(lmsCountParam.getDayOffset())) {
            dateString = LocalDate.now().minusDays(lmsCountParam.getDayOffset()).toString();
        }
        Long result = lmsOrderService.fetchOrderCount(dateString, lmsCountParam.getStatuses());
        return CommonResult.success(result);
    }
}

