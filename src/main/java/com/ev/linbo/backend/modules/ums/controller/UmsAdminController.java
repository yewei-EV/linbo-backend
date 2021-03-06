package com.ev.linbo.backend.modules.ums.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ev.linbo.backend.common.api.CommonPage;
import com.ev.linbo.backend.common.api.CommonResult;
import com.ev.linbo.backend.modules.ums.dto.UmsAdminLoginParam;
import com.ev.linbo.backend.modules.ums.dto.UmsAdminParam;
import com.ev.linbo.backend.modules.ums.dto.UmsAllocateParam;
import com.ev.linbo.backend.modules.ums.dto.UpdateAdminPasswordParam;
import com.ev.linbo.backend.modules.ums.model.UmsAdmin;
import com.ev.linbo.backend.modules.ums.model.UmsRole;
import com.ev.linbo.backend.modules.ums.service.UmsAdminService;
import com.ev.linbo.backend.modules.ums.service.UmsRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台用户管理
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UmsAdminService adminService;

    @Autowired
    private UmsRoleService roleService;

    @ApiOperation(value = "用户注册录入")
    @RequestMapping(value = "/input", method = RequestMethod.GET)
    @ResponseBody
    public void input() {
        try {
            adminService.input();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        umsAdminParam.setIcon("https://hypeeyes-linbo.oss-cn-hongkong.aliyuncs.com/attachment/B0478D3C-A50E-4EA0-BDA5-96F55AA2DCC7.jpg");
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return CommonResult.failed();
        }
        adminService.updateRole(umsAdmin.getId(), Collections.singletonList(9L));
        return CommonResult.success(umsAdmin);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAdminInfo(Principal principal) {
        if(principal==null){
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        data.put("id", umsAdmin.getId());
        data.put("region", umsAdmin.getRegion());
        data.put("userSn", umsAdmin.getUserSn());
        data.put("email", umsAdmin.getEmail());
        data.put("wechat", umsAdmin.getWechat());
        data.put("discordId", umsAdmin.getDiscordId());
        data.put("addressList", adminService.getAddressList(umsAdmin.getId()));

        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        if(CollUtil.isNotEmpty(roleList)){
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList());
            data.put("roles",roles);
        }
        return CommonResult.success(data);
    }

    @ApiOperation(value = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult logout() {
        return CommonResult.success(null);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "userSn", required = false) String userSn,
                                                   @RequestParam(value = "wechat", required = false) String wechat,
                                                   @RequestParam(value = "discordId", required = false) String discordId,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<UmsAdmin> adminList = adminService.list(keyword, userSn, wechat, discordId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation("获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getById(id);
        return CommonResult.success(admin);
    }

    @ApiOperation("修改指定用户信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody UmsAdmin admin) {
        boolean success = adminService.update(id, admin);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改指定用户密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam) {
        int status = adminService.updatePassword(updatePasswordParam);
        if (status > 0) {
            return CommonResult.success(status);
        } else if (status == -1) {
            return CommonResult.failed("提交参数不合法");
        } else if (status == -2) {
            return CommonResult.failed("找不到该用户");
        } else if (status == -3) {
            return CommonResult.failed("旧密码错误");
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除指定用户信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = adminService.delete(id);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改帐号状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Long id,@RequestParam(value = "status") Integer status) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(status);
        boolean success = adminService.update(id,umsAdmin);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改帐号个人资料")
    @RequestMapping(value = "/updateProfileInfo/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateProfileInfo(@PathVariable Long id, @RequestParam(value = "email") String email,
                                          @RequestParam(value = "wechat") String wechat,
                                          @RequestParam(value = "discordId") String discordId,
                                          @RequestParam(value = "icon") String icon) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setEmail(email);
        umsAdmin.setDiscordId(discordId);
        umsAdmin.setWechat(wechat);
        umsAdmin.setIcon(icon);
        boolean success = adminService.update(id, umsAdmin);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给用户分配角色")
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRole(@RequestParam("adminId") Long adminId,
                                   @RequestParam("roleIds") List<Long> roleIds) {
        int count = adminService.updateRole(adminId, roleIds);
        if (count >= 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给用户分配地址")
    @RequestMapping(value = "/allocateAddress/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult allocateAddress(@RequestBody UmsAllocateParam umsAllocateParam) {
        boolean result = adminService.allocateAddress(umsAllocateParam.getAdminId(), umsAllocateParam.getAddressId());
        if (result) {
            return CommonResult.success(true);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取指定用户的角色")
    @RequestMapping(value = "/role/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return CommonResult.success(roleList);
    }

    @ApiOperation("通过discord Id获取指定用户")
    @RequestMapping(value = "/discord/{discordId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsAdmin> getAdminByDiscordID(@PathVariable String discordId) {
        UmsAdmin adminList = adminService.getAdminByDiscordId(discordId);
        return CommonResult.success(adminList);
    }

    @ApiOperation("通过用户识别码获取指定用户")
    @RequestMapping(value = "/userSn/{userSn}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsAdmin> getAdminByUserSn(@PathVariable String userSn) {
        UmsAdmin adminList = adminService.getAdminByUserSn(userSn);
        return CommonResult.success(adminList);
    }
}
