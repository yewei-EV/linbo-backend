package com.macro.mall.tiny.modules.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.ums.model.UmsAnnouncement;

/**
 * 后台角色管理Service
 * Created by macro on 2018/9/30.
 */
public interface UmsAnnouncementService extends IService<UmsAnnouncement> {

    /**
     * 更新公告
     */
    boolean update(String announcement);

}
