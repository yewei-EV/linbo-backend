package com.ev.linbo.backend.modules.ums.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ev.linbo.backend.common.api.CommonPage;
import com.ev.linbo.backend.common.api.CommonResult;
import com.ev.linbo.backend.modules.ums.dto.OperateLogQueryParam;
import com.ev.linbo.backend.modules.ums.service.OperateLogInfoService;
import com.ev.linbo.backend.security.model.OperateLogInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 日志前端控制器
 * </p>
 *
 * @author Yewei Wang
 * @since 2021-05-15
 */
@Controller
@Api(tags = "OperateLogInfoController", description = "日志管理")
@RequestMapping("/log")
public class OperateLogInfoController {

    private final OperateLogInfoService operateLogInfoService;

    public OperateLogInfoController(OperateLogInfoService operateLogInfoService) {
        this.operateLogInfoService = operateLogInfoService;
    }

    @ApiOperation("根据条件分页获取日志列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<CommonPage<OperateLogInfo>> list(@RequestBody OperateLogQueryParam operateLogQueryParam) {
        Page<OperateLogInfo> logList = operateLogInfoService.list(operateLogQueryParam);
        return CommonResult.success(CommonPage.restPage(logList));
    }

}

