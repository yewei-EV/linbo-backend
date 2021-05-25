package com.macro.mall.tiny.modules.lms.mapper;

import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
public interface LmsOrderMapper extends BaseMapper<LmsOrder> {

    /**
     * 获取货物所有订单
     */
    List<LmsOrder> getOrderList(@Param("itemId") Long itemId);

}
