package com.ev.linbo.backend.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ev.linbo.backend.modules.ums.mapper.UmsAddressMapper;
import com.ev.linbo.backend.modules.ums.model.UmsAddress;
import com.ev.linbo.backend.modules.ums.model.UmsAdminAddressRelation;
import com.ev.linbo.backend.modules.ums.service.UmsAddressService;
import com.ev.linbo.backend.modules.ums.service.UmsAdminAddressRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台角色管理Service实现类
 * Created by macro on 2018/9/30.
 */
@Service
public class UmsAddressServiceImpl extends ServiceImpl<UmsAddressMapper, UmsAddress>implements UmsAddressService {

    @Autowired
    private UmsAdminAddressRelationService adminAddressRelationService;

    @Override
    public boolean create(UmsAddress address) {
        return save(address);
    }

    @Override
    public boolean delete(List<Long> ids) {
        QueryWrapper<UmsAdminAddressRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminAddressRelation::getAddressId, ids.get(0));
        adminAddressRelationService.remove(wrapper);
        return removeByIds(ids);
    }

}
