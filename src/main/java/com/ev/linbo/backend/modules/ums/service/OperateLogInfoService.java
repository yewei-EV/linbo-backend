package com.ev.linbo.backend.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ev.linbo.backend.modules.ums.dto.OperateLogQueryParam;
import com.ev.linbo.backend.security.model.OperateLogInfo;

/**
 * 后台角色管理Service
 * Created by macro on 2018/9/30.
 */
public interface OperateLogInfoService extends IService<OperateLogInfo> {

    Page<OperateLogInfo> list(OperateLogQueryParam operateLogQueryParam);

}
