package com.macro.mall.tiny.common.service;

import com.macro.mall.tiny.common.domain.OssCallbackResult;
import com.macro.mall.tiny.common.domain.OssPolicyResult;

import javax.servlet.http.HttpServletRequest;

/**
 * oss上传管理Service
 */
public interface CommonService {

    /**
     * oss上传成功回调
     */
    String generateRandomStringBySeed(final int seed);
}
