package com.ev.linbo.backend.modules.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ev.linbo.backend.modules.ums.mapper.UmsAnnouncementMapper;
import com.ev.linbo.backend.modules.ums.model.UmsAnnouncement;
import com.ev.linbo.backend.modules.ums.service.UmsAnnouncementService;
import org.springframework.stereotype.Service;

/**
 * 后台角色管理Service实现类
 * Created by macro on 2018/9/30.
 */
@Service
public class UmsAnnouncementServiceImpl extends ServiceImpl<UmsAnnouncementMapper, UmsAnnouncement>implements UmsAnnouncementService {

    @Override
    public boolean update(String announcement) {
        UmsAnnouncement umsAnnouncement = this.getById(1);
        umsAnnouncement.setAnnouncement(announcement);
        return this.updateById(umsAnnouncement);
    }
}
