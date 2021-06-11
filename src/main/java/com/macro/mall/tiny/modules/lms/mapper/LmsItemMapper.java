package com.macro.mall.tiny.modules.lms.mapper;

import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 货物表 Mapper 接口
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
public interface LmsItemMapper extends BaseMapper<LmsItem> {

    /**
     * 获取货物统计
     */
    Long getItemCount(@Param("location") String location, @Param("date") String date, @Param("status") String status);

}
