package com.macro.mall.tiny.modules.lms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.lms.mapper.LmsOrderMapper;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.macro.mall.tiny.modules.lms.mapper.LmsItemMapper;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.service.LmsItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

/**
 * <p>
 * 货物表 服务实现类
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
@Service
public class LmsItemServiceImpl extends ServiceImpl<LmsItemMapper, LmsItem> implements LmsItemService {
    @Autowired
    private LmsOrderMapper lmsOrderMapper;

    @Override
    public boolean create(LmsItem item) {
        item.setCreateTime(new Date());
        return save(item);
    }

    @Override
    public boolean delete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public Page<LmsItem> list(String locator, String deliverySn, String userSn, String location, String note,
                              String createTime, String sku, String size, Integer status, Integer pageSize,
                              Integer pageNum) {
        Page<LmsItem> page = new Page<>(pageNum,pageSize);
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();

        if(StrUtil.isNotEmpty(locator)){
            lambda.eq(LmsItem::getLocator, locator);
        }
        if(StrUtil.isNotEmpty(deliverySn)){
            lambda.like(LmsItem::getDeliverySn, deliverySn);
        }
        if(StrUtil.isNotEmpty(userSn)){
            lambda.eq(LmsItem::getUserSn, userSn);
        }
        if(StrUtil.isNotEmpty(location)){
            lambda.eq(LmsItem::getLocation, location);
        }
        if(StrUtil.isNotEmpty(note)){
            lambda.like(LmsItem::getNote, note);
        }
        if(StrUtil.isNotEmpty(createTime)){
            lambda.like(LmsItem::getCreateTime, createTime);
        }
        if(StrUtil.isNotEmpty(sku)){
            lambda.eq(LmsItem::getSku, sku);
        }
        if(StrUtil.isNotEmpty(size)){
            lambda.eq(LmsItem::getSize, size);
        }
        if(status!=null){
            lambda.eq(LmsItem::getStatus, status);
        }
        return page(page,wrapper);
    }

    @Override
    public List<LmsOrder> getOrderList(Long itemId) {
        return lmsOrderMapper.getOrderList(itemId);
    }

}
