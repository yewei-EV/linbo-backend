package com.macro.mall.tiny.modules.lms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.lms.mapper.LmsOrderMapper;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.macro.mall.tiny.modules.lms.mapper.LmsItemMapper;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.model.LmsOrderItemRelation;
import com.macro.mall.tiny.modules.lms.service.LmsItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.lms.service.LmsOrderItemRelationService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final LmsOrderMapper lmsOrderMapper;

    private final LmsOrderItemRelationService lmsOrderItemRelationService;

    public LmsItemServiceImpl(LmsOrderMapper lmsOrderMapper, LmsOrderItemRelationService lmsOrderItemRelationService) {
        this.lmsOrderMapper = lmsOrderMapper;
        this.lmsOrderItemRelationService = lmsOrderItemRelationService;
    }

    @Override
    public boolean create(LmsItem item) {
        item.setCreateTime(new Date());
        return save(item);
    }

    @Override
    public boolean delete(List<Long> ids) {
        for (Long id : ids) {
            QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(LmsOrderItemRelation::getItemId,id);
            lmsOrderItemRelationService.remove(wrapper);
        }
        return removeByIds(ids);
    }

    @Override
    public Page<LmsItem> list(String deliverySn, String userSn, String location, String note, String createTime,
                              String sku, String size, Integer status, String positionInfo, Integer pageSize,
                              Integer pageNum) {
        Page<LmsItem> page = new Page<>(pageNum,pageSize);
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();

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
        if(StrUtil.isNotEmpty(positionInfo)){
            lambda.like(LmsItem::getPositionInfo, positionInfo);
        }
        return page(page,wrapper);
    }

    @Override
    public Page<LmsItem> listPrecise(String deliverySn, String userSn, String location, String note, String createTime,
                              String sku, String size, Integer status, String positionInfo, Integer pageSize,
                              Integer pageNum) {
        Page<LmsItem> page = new Page<>(pageNum,pageSize);
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();

        if(StrUtil.isNotEmpty(deliverySn)){
            lambda.eq(LmsItem::getDeliverySn, deliverySn);
        }
        if(StrUtil.isNotEmpty(userSn)){
            lambda.eq(LmsItem::getUserSn, userSn);
        }
        if(StrUtil.isNotEmpty(location)){
            lambda.eq(LmsItem::getLocation, location);
        }
        if(StrUtil.isNotEmpty(note)){
            lambda.eq(LmsItem::getNote, note);
        }
        if(StrUtil.isNotEmpty(createTime)){
            lambda.eq(LmsItem::getCreateTime, createTime);
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
        if(StrUtil.isNotEmpty(positionInfo)){
            lambda.eq(LmsItem::getPositionInfo, positionInfo);
        }
        return page(page,wrapper);
    }

    @Override
    public List<LmsOrder> getOrderList(Long itemId) {
        return lmsOrderMapper.getOrderList(itemId);
    }

    @Override
    public boolean allocateOrder(Long itemId, Long orderId) {
        //先删除原来的关系
        QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LmsOrderItemRelation::getOrderId, orderId);
        lmsOrderItemRelationService.remove(wrapper);
        //建立新关系
        if (orderId!=null) {
            List<LmsOrderItemRelation> list = new ArrayList<>();
            LmsOrderItemRelation itemRelation = new LmsOrderItemRelation();
            itemRelation.setItemId(itemId);
            itemRelation.setOrderId(orderId);
            list.add(itemRelation);
            lmsOrderItemRelationService.saveBatch(list);
            return true;
        }
        return false;
    }

}
