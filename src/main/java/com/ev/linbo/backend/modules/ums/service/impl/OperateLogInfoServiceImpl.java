package com.ev.linbo.backend.modules.ums.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ev.linbo.backend.modules.ums.dto.OperateLogQueryParam;
import com.ev.linbo.backend.modules.ums.mapper.OperateLogInfoMapper;
import com.ev.linbo.backend.modules.ums.service.OperateLogInfoService;
import com.ev.linbo.backend.security.model.OperateLogInfo;
import org.springframework.stereotype.Service;

/**
 * @author Yewei Wang
 */
@Service
public class OperateLogInfoServiceImpl extends ServiceImpl<OperateLogInfoMapper, OperateLogInfo> implements OperateLogInfoService {

    @Override
    public Page<OperateLogInfo> list(OperateLogQueryParam operateLogQueryParam) {
        Page<OperateLogInfo> page = new Page<>(operateLogQueryParam.getPageNum(), operateLogQueryParam.getPageSize());
        QueryWrapper<OperateLogInfo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("execute_time");
        LambdaQueryWrapper<OperateLogInfo> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(operateLogQueryParam.getUserName())){
            lambda.like(OperateLogInfo::getUserName, operateLogQueryParam.getUserName());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getOperation())){
            lambda.like(OperateLogInfo::getOperation, operateLogQueryParam.getOperation());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getMethod())){
            lambda.like(OperateLogInfo::getMethod, operateLogQueryParam.getMethod());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getModifiedData())){
            lambda.like(OperateLogInfo::getModifiedData, operateLogQueryParam.getModifiedData());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getPreModifiedData())){
            lambda.like(OperateLogInfo::getPreModifiedData, operateLogQueryParam.getPreModifiedData());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getResult())){
            lambda.like(OperateLogInfo::getResult, operateLogQueryParam.getResult());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getIp())){
            lambda.like(OperateLogInfo::getIp, operateLogQueryParam.getIp());
        }
        if (StrUtil.isNotEmpty(operateLogQueryParam.getOperateType())){
            lambda.like(OperateLogInfo::getOperateType, operateLogQueryParam.getOperateType());
        }

        return page(page,wrapper);
    }
}
