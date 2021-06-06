package com.macro.mall.tiny.common.service;

import com.macro.mall.tiny.common.domain.OssCallbackResult;
import com.macro.mall.tiny.common.domain.OssPolicyResult;

import javax.servlet.http.HttpServletRequest;

/**
 * oss上传管理Service
 */
public interface OssService {
    /**
     * oss上传策略生成
     */
    OssPolicyResult policy();

    /**
     * oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);
}
