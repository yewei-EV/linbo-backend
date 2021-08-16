package com.ev.linbo.backend.modules.ums.controller;

import com.ev.linbo.backend.common.api.CommonResult;
import com.ev.linbo.backend.modules.ums.model.UmsAnnouncement;
import com.ev.linbo.backend.modules.ums.service.UmsAnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 公告管理
 * Created by macro on 2018/9/30.
 */
@Controller
@Api(tags = "UmsAnnouncementController", description = "公告管理")
@RequestMapping("/announcement")
public class UmsAnnouncementController {

    @Autowired
    private UmsAnnouncementService umsAnnouncementService;

    @ApiOperation(value = "获取当前公告")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> getAnnouncementInfo() {
        UmsAnnouncement an = umsAnnouncementService.getById(1);
        return CommonResult.success(an.getAnnouncement());
    }


    @ApiOperation("修改公告")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@RequestBody UmsAnnouncement umsAnnouncement) {
        boolean success = umsAnnouncementService.update(umsAnnouncement.getAnnouncement());
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }


}
