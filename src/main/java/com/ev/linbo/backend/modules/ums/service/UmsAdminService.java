package com.ev.linbo.backend.modules.ums.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ev.linbo.backend.modules.ums.dto.UmsAdminParam;
import com.ev.linbo.backend.modules.ums.dto.UpdateAdminPasswordParam;
import com.ev.linbo.backend.modules.ums.model.UmsAddress;
import com.ev.linbo.backend.modules.ums.model.UmsAdmin;
import com.ev.linbo.backend.modules.ums.model.UmsResource;
import com.ev.linbo.backend.modules.ums.model.UmsRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * 后台管理员管理Service
 * Created by macro on 2018/4/26.
 */
public interface UmsAdminService extends IService<UmsAdmin> {
    /**
     * 根据用户名获取后台管理员
     */
    UmsAdmin getAdminByUsername(String username);

    /**
     * 根据Discord ID获取用户
     */
    UmsAdmin getAdminByDiscordId(String discordId);

    /**
     * 根据用户识别码获取用户
     */
    UmsAdmin getAdminByUserSn(String userSn);

    /**
     * 注册功能
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 录入功能
     */
    void input() throws IOException;

    /**
     * 登录功能
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT的token
     */
    String login(String username,String password);

    /**
     * 刷新token的功能
     * @param oldToken 旧的token
     */
    String refreshToken(String oldToken);

    /**
     * 根据用户名或昵称分页查询用户
     */
    Page<UmsAdmin> list(String keyword, String userSn, String wechat, String discordId, Integer pageSize, Integer pageNum);

    /**
     * 修改指定用户信息
     */
    boolean update(Long id, UmsAdmin admin);

    /**
     * 删除指定用户
     */
    boolean delete(Long id);

    /**
     * 修改用户角色关系
     */
    @Transactional
    int updateRole(Long adminId, List<Long> roleIds);

    /**
     * 修改用户地址关系
     */
    @Transactional
    boolean allocateAddress(Long adminId, Long addressId);

    /**
     * 获取用户对于角色
     */
    List<UmsRole> getRoleList(Long adminId);

    /**
     * 获取用户地址对于角色
     */
    List<UmsAddress> getAddressList(Long adminId);

    /**
     * 获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);

    /**
     * 修改密码
     */
    int updatePassword(UpdateAdminPasswordParam updatePasswordParam);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

}
