package com.ev.linbo.backend.common.service;

import com.ev.linbo.backend.common.domain.OssCallbackResult;
import com.ev.linbo.backend.common.domain.OssPolicyResult;

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
