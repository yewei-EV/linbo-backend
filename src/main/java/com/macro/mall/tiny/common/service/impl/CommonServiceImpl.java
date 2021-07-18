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
package com.macro.mall.tiny.common.service.impl;

import com.macro.mall.tiny.common.service.CommonService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Yewei Wang
 */
@Service
public class CommonServiceImpl implements CommonService {

    private static final String[] GENERATE_SOURCE = new String[]{"X","2","Q","5","W","6","A","R","7","S","K","G","T",
            "4","P","3","B","C","H","N","F","8","M","D","9","Y","V","E","J","Z","U"};

    @Override
    public String generateRandomStringBySeed(int seed) {
        List<String> src = Arrays.asList(GENERATE_SOURCE);
        Collections.shuffle(src);
        StringBuilder randomSb = new StringBuilder(4);
        int i2 = (seed) % 30;
        int i1 = (seed / (30)) % 30;
        int i0 = (seed / (30 * 30)) % 30;
        int i = (seed / (30 * 30 * 30)) % 30;
        randomSb = randomSb.append(src.get(i0)).append(src.get(i1)).append(src.get(i2)).append(src.get(i));
        return randomSb.toString();
    }
}
