package com.macro.mall.tiny.modules.lms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.mapper.LmsOrderMapper;
import com.macro.mall.tiny.modules.lms.model.LmsOrderItemRelation;
import com.macro.mall.tiny.modules.lms.service.LmsItemService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderItemRelationService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
@Service
public class LmsOrderServiceImpl extends ServiceImpl<LmsOrderMapper, LmsOrder> implements LmsOrderService {

    private final LmsItemService lmsItemService;

    private final LmsOrderItemRelationService lmsOrderItemRelationService;

    public LmsOrderServiceImpl(LmsItemService lmsItemService, LmsOrderItemRelationService lmsOrderItemRelationService) {
        this.lmsItemService = lmsItemService;
        this.lmsOrderItemRelationService = lmsOrderItemRelationService;
    }


    @Override
    public boolean create(LmsOrder order) {
        order.setCreateTime(new Date());
        return save(order);
    }

    @Override
    public boolean delete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public Page<LmsOrder> list(Long id, String action, String deliverySn, String userSn, String destination,
                               String note, String createTime, Integer status, Integer paymentStatus, String paymentTime,
                               Integer pageSize, Integer pageNum) {
        Page<LmsOrder> page = new Page<>(pageNum,pageSize);
        QueryWrapper<LmsOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        LambdaQueryWrapper<LmsOrder> lambda = wrapper.lambda();
        if(id!=null){
            lambda.eq(LmsOrder::getId, id);
        }
        if(StrUtil.isNotEmpty(action)){
            lambda.eq(LmsOrder::getAction, action);
        }
        if(StrUtil.isNotEmpty(deliverySn)){
            lambda.like(LmsOrder::getDeliverySn, deliverySn);
        }
        if(StrUtil.isNotEmpty(userSn)){
            lambda.eq(LmsOrder::getUserSn, userSn);
        }
        if(StrUtil.isNotEmpty(destination)){
            lambda.like(LmsOrder::getDestination, destination);
        }
        if(StrUtil.isNotEmpty(note)){
            lambda.like(LmsOrder::getNote, note);
        }
        if(StrUtil.isNotEmpty(createTime)){
            lambda.like(LmsOrder::getCreateTime, createTime);
        }
        if(status!=null){
            lambda.eq(LmsOrder::getStatus, status);
        }
        if(paymentStatus!=null){
            lambda.eq(LmsOrder::getPaymentStatus, paymentStatus);
        }
        if(StrUtil.isNotEmpty(paymentTime)){
            lambda.like(LmsOrder::getPaymentTime, paymentTime);
        }

        return page(page,wrapper);
    }

    @Override
    public boolean refreshItemsStatusByOrder(Long orderId) {
        LmsOrder order = this.getById(orderId);
        QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LmsOrderItemRelation::getOrderId, orderId);
        List<LmsOrderItemRelation> list = lmsOrderItemRelationService.list(wrapper);
        if (!CollectionUtil.isEmpty(list)) {
            for (LmsOrderItemRelation lmsOrderItemRelation : list) {
                LmsItem item = lmsItemService.getById(lmsOrderItemRelation.getItemId());
                item.setStatus(order.getStatus());
                lmsItemService.updateById(item);
            }
        }
        return true;
    }

}
