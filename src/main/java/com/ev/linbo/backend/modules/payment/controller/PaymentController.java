/*
 * Copyright (c) 2016 Tianbao Travel Ltd.
 * www.tianbaotravel.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Tianbao Travel Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with Tianbao Travel Ltd.
 */
package com.ev.linbo.backend.modules.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.ev.linbo.backend.common.api.CommonResult;
import com.ev.linbo.backend.modules.lms.model.LmsItem;
import com.ev.linbo.backend.modules.lms.model.LmsOrder;
import com.ev.linbo.backend.modules.lms.service.LmsItemService;
import com.ev.linbo.backend.modules.lms.service.LmsOrderService;
import com.ev.linbo.backend.modules.payment.config.AlipayConfig;
import com.ev.linbo.backend.modules.payment.entity.AlipayBean;
import com.ev.linbo.backend.modules.payment.entity.AlipayNotifyParam;
import com.ev.linbo.backend.modules.payment.entity.AlipayParam;
import com.ev.linbo.backend.modules.payment.service.PaymentService;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yewei Wang
 */
@Controller
@Api(tags = "AlipayController", description = "付款管理")
@RequestMapping("/alipay")
public class PaymentController {

    private final AlipayConfig alipayConfig;

    private final PaymentService paymentService;

    private final LmsOrderService lmsOrderService;

    private final LmsItemService lmsItemService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    public PaymentController(AlipayConfig alipayConfig, PaymentService paymentService, LmsOrderService lmsOrderService, LmsItemService lmsItemService) {
        this.alipayConfig = alipayConfig;
        this.paymentService = paymentService;
        this.lmsOrderService = lmsOrderService;
        this.lmsItemService = lmsItemService;
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> alipay(@RequestBody AlipayParam alipayParam) {
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(alipayParam.getOutTradeNo());
        alipayBean.setSubject(alipayParam.getSubject());
        alipayBean.setTotal_amount(Double.parseDouble(alipayParam.getTotalAmount()));
        alipayBean.setBody(alipayParam.getBody());
        return CommonResult.success(paymentService.toAlipay(alipayBean));
    }

    /**
     * <pre>
     * 第一步:验证签名,签名通过后进行第二步
     * 第二步:按一下步骤进行验证
     * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
     * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
     * 3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
     * 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
     * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
     * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
     * </pre>
     */
    @RequestMapping("alipay_callback")
    @ResponseBody
    public String callback(HttpServletRequest request) {
        Map<String, String> params = convertRequestParamsToMap(request); // 将异步通知中收到的待验证所有参数都存放到map中
        String paramsJson = JSON.toJSONString(params);
        System.out.println("支付宝回调，" + paramsJson);
        try {
            AlipayConfig alipayConfig = new AlipayConfig();// 支付宝配置
            // 调用SDK验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipay_public_key(),
                    alipayConfig.getCharset(), alipayConfig.getSign_type());
            if (signVerified) {
                System.out.println("支付宝回调签名认证成功");
                // 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
                this.check(params);
                // 另起线程处理业务
                executorService.execute(() -> {
                    AlipayNotifyParam param = buildAlipayNotifyParam(params);
                    String trade_status = param.getTradeStatus();
                    // 支付成功
                    if (trade_status.equals("TRADE_SUCCESS")
                            || trade_status.equals("TRADE_FINISHED")) {
                        // 处理支付成功逻辑
                        try {
                            // 业务逻辑
                            LmsOrder order = lmsOrderService.getById(params.get("out_trade_no"));
                            order.setOrderStatus(2);
                            if (StringUtils.isEmpty(order.getNote())) {
                                order.setNote(params.get("trade_no"));
                            } else {
                                order.setNote(order.getNote() + "," + params.get("trade_no"));
                            }
                            order.setPaymentTime(new Date());
                            lmsOrderService.save(order);
                            List<LmsItem> items = lmsItemService.getItemListByOrder(order.getId());
                            for (LmsItem item : items) {
                                lmsItemService.updateItemStatus(item, order.getOrderAction());
                            }
                        } catch (Exception e) {
                            System.out.println("支付宝回调业务处理报错,params:" + paramsJson + e);
                        }
                    } else {
                        System.out.println("没有处理支付宝回调业务，支付宝交易状态：{},params:{}" + trade_status + paramsJson);
                    }
                });
                // 如果签名验证正确，立即返回success，后续业务另起线程单独处理
                // 业务处理失败，可查看日志进行补偿，跟支付宝已经没多大关系。
                return "success";
            } else {
                System.out.println("支付宝回调签名认证失败，signVerified=false, paramsJson:{}" + paramsJson);
                return "failure";
            }
        } catch (AlipayApiException e) {
            System.out.println("支付宝回调签名认证失败,paramsJson:{},errorMsg:{}" + paramsJson + e.getMessage());
            return "failure";
        }
    }

    // 将request中的参数转换成Map
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<>();

        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();

        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;

            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.substring(1));
            } else {
                retMap.put(name, "");
            }
        }

        return retMap;
    }

    private AlipayNotifyParam buildAlipayNotifyParam(Map<String, String> params) {
        String json = JSON.toJSONString(params);
        return JSON.parseObject(json, AlipayNotifyParam.class);
    }

    /**
     * 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
     * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
     * 3、校验通知中的seller_id（或者seller_email)是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
     * 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
     * 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
     * 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
     *
     * @param params
     * @throws AlipayApiException
     */
    private void check(Map<String, String> params) throws AlipayApiException {
        String outTradeNo = params.get("out_trade_no");

        // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        LmsOrder order = lmsOrderService.getById(outTradeNo);
        if (order == null) {
            throw new AlipayApiException("out_trade_no错误");
        }

        // 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        BigDecimal total_amount = new BigDecimal(params.get("total_amount"));
        if (!total_amount.equals(order.getPrice()) && !total_amount.equals(order.getSfPrice())) {
            throw new AlipayApiException("error total_amount");
        }

        // 4、验证app_id是否为该商户本身。
        if (!params.get("app_id").equals(alipayConfig.getApp_id())) {
            throw new AlipayApiException("app_id不一致");
        }
    }

}
