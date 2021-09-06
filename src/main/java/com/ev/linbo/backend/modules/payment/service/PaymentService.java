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
package com.ev.linbo.backend.modules.payment.service;

import com.alipay.api.AlipayApiException;
import com.ev.linbo.backend.modules.payment.entity.AlipayBean;

/**
 * @author Yewei Wang
 */
public interface PaymentService {

    /**
     * 支付宝支付接口
     * @param alipayBean
     * @return
     * @throws AlipayApiException
     */
    String toAlipay(AlipayBean alipayBean);

}
