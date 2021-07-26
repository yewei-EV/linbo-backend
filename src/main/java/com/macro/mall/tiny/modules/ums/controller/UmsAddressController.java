package com.macro.mall.tiny.modules.ums.controller;

import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.modules.ums.model.UmsAddress;
import com.macro.mall.tiny.modules.ums.service.UmsAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * 后台用户角色管理
 * Created by macro on 2018/9/30.
 */
@Controller
@Api(tags = "UmsAddressController", description = "后台用户地址管理")
@RequestMapping("/address")
public class UmsAddressController {

    @Autowired
    private UmsAddressService addressService;

    @ApiOperation("添加地址")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody UmsAddress address) {
        boolean success = addressService.create(address);
        if (success) {
            return CommonResult.success(address.getId());
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改订单")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody UmsAddress address) {
        address.setId(id);
        boolean success = addressService.updateById(address);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除角色")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = addressService.delete(Collections.singletonList(id));
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }


}
