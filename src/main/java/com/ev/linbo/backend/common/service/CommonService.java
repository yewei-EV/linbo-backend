package com.ev.linbo.backend.common.service;

/**
 * oss上传管理Service
 */
public interface CommonService {

    /**
     * oss上传成功回调
     */
    String generateRandomStringBySeed(final int seed);
}
