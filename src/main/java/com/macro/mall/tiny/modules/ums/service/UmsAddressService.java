package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.ums.model.UmsAddress;

import java.util.List;

/**
 * 后台角色管理Service
 * Created by macro on 2018/9/30.
 */
public interface UmsAddressService extends IService<UmsAddress> {
    /**
     * 添加地址
     */
    boolean create(UmsAddress address);

    /**
     * 批量删除地址
     */
    boolean delete(List<Long> ids);

}
