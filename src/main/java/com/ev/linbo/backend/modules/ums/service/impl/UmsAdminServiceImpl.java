package com.ev.linbo.backend.modules.ums.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ev.linbo.backend.common.service.CommonService;
import com.ev.linbo.security.util.JwtTokenUtil;
import com.ev.linbo.backend.common.exception.Asserts;
import com.ev.linbo.backend.domain.AdminUserDetails;
import com.ev.linbo.backend.modules.ums.dto.UmsAdminParam;
import com.ev.linbo.backend.modules.ums.dto.UpdateAdminPasswordParam;
import com.ev.linbo.backend.modules.ums.mapper.*;
import com.ev.linbo.backend.modules.ums.model.*;
import com.ev.linbo.backend.modules.ums.service.UmsAdminAddressRelationService;
import com.ev.linbo.backend.modules.ums.service.UmsAdminCacheService;
import com.ev.linbo.backend.modules.ums.service.UmsAdminRoleRelationService;
import com.ev.linbo.backend.modules.ums.service.UmsAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台管理员管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper,UmsAdmin> implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsAdminLoginLogMapper loginLogMapper;
    @Autowired
    private UmsAdminCacheService adminCacheService;
    @Autowired
    private UmsAdminRoleRelationService adminRoleRelationService;
    @Autowired
    private UmsAdminAddressRelationService adminAddressRelationService;
    @Autowired
    private UmsRoleMapper roleMapper;
    @Autowired
    private UmsAddressMapper addressMapper;
    @Autowired
    private UmsResourceMapper resourceMapper;
    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private CommonService commonService;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin admin = adminCacheService.getAdmin(username);
        if(admin!=null) return  admin;
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,username);
        List<UmsAdmin> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            admin = adminList.get(0);
            adminCacheService.setAdmin(admin);
            return admin;
        }
        return null;
    }

    @Override
    public UmsAdmin getAdminByDiscordId(String discordId) {
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getDiscordId, discordId);
        List<UmsAdmin> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            return adminList.get(0);
        }
        return null;
    }

    @Override
    public UmsAdmin getAdminByUserSn(String userSn) {
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUserSn, userSn);
        List<UmsAdmin> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            return adminList.get(0);
        }
        return null;
    }

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername, umsAdmin.getUsername());
        List<UmsAdmin> umsAdminList = list(wrapper);
        if (umsAdminList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);

        //查询是否有相同的User Sn
        QueryWrapper<UmsAdmin> snWrapper = new QueryWrapper<>();
        snWrapper.lambda().eq(UmsAdmin::getDiscordId, umsAdmin.getDiscordId());
        List<UmsAdmin> existUmsAdminList = list(snWrapper);
        if (existUmsAdminList.size() > 0) {
            existUmsAdminList.get(0).setCreateTime(new Date());
            existUmsAdminList.get(0).setUsername(umsAdminParam.getUsername());
            existUmsAdminList.get(0).setPassword(encodePassword);
            existUmsAdminList.get(0).setEmail(umsAdminParam.getEmail());
            existUmsAdminList.get(0).setStatus(1);
            umsAdmin = existUmsAdminList.get(0);
            this.delete(existUmsAdminList.get(0).getId());
        } else {
            Long userCount = adminMapper.getAdminCount();
            umsAdmin.setUserSn(commonService.generateRandomStringBySeed(Math.toIntExact(userCount)));
        }
        baseMapper.insert(umsAdmin);
        return umsAdmin;
    }

    @Override
    public void input() throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader("/Users/kiwi/Downloads/useSn.csv"));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            // do something with the data
            if (data[0].equals("识别码") || data.length < 2 || data[1].contains("xF0")) {
                continue;
            }
            UmsAdmin umsAdmin = new UmsAdmin();
            umsAdmin.setStatus(0);
            umsAdmin.setDiscordId(data[1]);
            umsAdmin.setUserSn(data[0]);
            baseMapper.insert(umsAdmin);
        }
        csvReader.close();
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                Asserts.fail("密码不正确");
            }
            if(!userDetails.isEnabled()){
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
//            updateLoginTimeByUsername(username);
            insertLoginLog(username);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    /**
     * 添加登录记录
     * @param username 用户名
     */
    private void insertLoginLog(String username) {
        UmsAdmin admin = getAdminByUsername(username);
        if(admin==null) return;
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(admin.getId());
        loginLog.setCreateTime(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(request.getRemoteAddr());
        loginLogMapper.insert(loginLog);
    }

    /**
     * 根据用户名修改登录时间
     */
    private void updateLoginTimeByUsername(String username) {
        UmsAdmin record = new UmsAdmin();
        record.setLoginTime(new Date());
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,username);
        update(record,wrapper);
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public Page<UmsAdmin> list(String keyword, String userSn, String discordId, Integer pageSize, Integer pageNum) {
        Page<UmsAdmin> page = new Page<>(pageNum,pageSize);
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<UmsAdmin> lambda = wrapper.lambda();
        if(StrUtil.isNotEmpty(keyword)){
            lambda.like(UmsAdmin::getUsername,keyword);
        }
        if(StrUtil.isNotEmpty(userSn)){
            lambda.like(UmsAdmin::getUserSn,userSn);
        }
        if(StrUtil.isNotEmpty(discordId)){
            lambda.like(UmsAdmin::getDiscordId,discordId);
        }
        return page(page,wrapper);
    }

    @Override
    public boolean update(Long id, UmsAdmin admin) {
        admin.setId(id);
        UmsAdmin rawAdmin = getById(id);
        if(rawAdmin.getPassword().equals(admin.getPassword())){
            //与原加密密码相同的不需要修改
            admin.setPassword(null);
        }else{
            //与原加密密码不同的需要加密修改
            if(StrUtil.isEmpty(admin.getPassword())){
                admin.setPassword(null);
            }else{
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        boolean success = updateById(admin);
        adminCacheService.delAdmin(id);
        return success;
    }

    @Override
    public boolean delete(Long id) {
        adminCacheService.delAdmin(id);
        boolean success = removeById(id);
        adminCacheService.delResourceList(id);
        return success;
    }

    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();
        //先删除原来的关系
        QueryWrapper<UmsAdminRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminRoleRelation::getAdminId,adminId);
        adminRoleRelationService.remove(wrapper);
        //建立新关系
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                list.add(roleRelation);
            }
            adminRoleRelationService.saveBatch(list);
        }
        adminCacheService.delResourceList(adminId);
        return count;
    }

    @Override
    public boolean allocateAddress(Long adminId, Long addressId) {
        QueryWrapper<UmsAdminAddressRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminAddressRelation::getAdminId, adminId);
        if (addressId!=null) {
            List<UmsAdminAddressRelation> list = new ArrayList<>();
            UmsAdminAddressRelation relation = new UmsAdminAddressRelation();
            relation.setAdminId(adminId);
            relation.setAddressId(addressId);
            list.add(relation);
            adminAddressRelationService.saveBatch(list);
            return true;
        }
        return false;
    }

    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return roleMapper.getRoleList(adminId);
    }

    @Override
    public List<UmsAddress> getAddressList(Long adminId) {
        return addressMapper.getAddressList(adminId);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        List<UmsResource> resourceList = adminCacheService.getResourceList(adminId);
        if(CollUtil.isNotEmpty(resourceList)){
            return  resourceList;
        }
        resourceList = resourceMapper.getResourceList(adminId);
        if(CollUtil.isNotEmpty(resourceList)){
            adminCacheService.setResourceList(adminId,resourceList);
        }
        return resourceList;
    }

    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        if(StrUtil.isEmpty(param.getUsername())
                ||StrUtil.isEmpty(param.getOldPassword())
                ||StrUtil.isEmpty(param.getNewPassword())){
            return -1;
        }
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername,param.getUsername());
        List<UmsAdmin> adminList = list(wrapper);
        if(CollUtil.isEmpty(adminList)){
            return -2;
        }
        UmsAdmin umsAdmin = adminList.get(0);
        if(!passwordEncoder.matches(param.getOldPassword(),umsAdmin.getPassword())){
            return -3;
        }
        umsAdmin.setPassword(passwordEncoder.encode(param.getNewPassword()));
        updateById(umsAdmin);
        adminCacheService.delAdmin(umsAdmin.getId());
        return 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        //获取用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null) {
            List<UmsResource> resourceList = getResourceList(admin.getId());
            return new AdminUserDetails(admin,resourceList);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }
}