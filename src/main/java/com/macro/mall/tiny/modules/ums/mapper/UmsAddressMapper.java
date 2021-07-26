package com.macro.mall.tiny.modules.ums.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 后台用户角色表 Mapper 接口
 * </p>
 *
 * @author macro
 * @since 2020-08-21
 */
public interface UmsAddressMapper extends BaseMapper<UmsAddress> {

    /**
     * 获取用户所有角色
     */
    List<UmsAddress> getAddressList(@Param("adminId") Long adminId);

}
