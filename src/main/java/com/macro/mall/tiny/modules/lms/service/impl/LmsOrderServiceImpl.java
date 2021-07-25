package com.macro.mall.tiny.modules.lms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.lms.mapper.LmsOrderMapper;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.macro.mall.tiny.modules.lms.model.LmsOrderItemRelation;
import com.macro.mall.tiny.modules.lms.service.LmsOrderItemRelationService;
import com.macro.mall.tiny.modules.lms.service.LmsOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

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

    private final LmsOrderItemRelationService lmsOrderItemRelationService;

    private final LmsOrderMapper lmsOrderMapper;

    public LmsOrderServiceImpl(LmsOrderItemRelationService lmsOrderItemRelationService, LmsOrderMapper lmsOrderMapper) {
        this.lmsOrderItemRelationService = lmsOrderItemRelationService;
        this.lmsOrderMapper = lmsOrderMapper;
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
    public Page<LmsOrder> list(Long id, String orderAction, String deliverySn, String userSn, String destination,
                               String note, String createTime, Integer orderStatus, Integer paymentStatus, String paymentTime,
                               Integer pageSize, Integer pageNum) {
        Page<LmsOrder> page = new Page<>(pageNum,pageSize);
        QueryWrapper<LmsOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        LambdaQueryWrapper<LmsOrder> lambda = wrapper.lambda();
        if(id!=null){
            lambda.eq(LmsOrder::getId, id);
        }
        if(StrUtil.isNotEmpty(orderAction)){
            lambda.eq(LmsOrder::getOrderAction, orderAction);
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
        if(orderStatus!=null){
            lambda.eq(LmsOrder::getOrderStatus, orderStatus);
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
    public Float fetchOrderPriceCount(String location, String date) {
        return lmsOrderMapper.getOrderPriceCount(location, date);
    }

    @Override
    public boolean checkIfPaid(Long itemId) {
        QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LmsOrderItemRelation::getItemId, itemId);
        List<LmsOrderItemRelation> list = lmsOrderItemRelationService.list(wrapper);
        if (!CollectionUtils.isEmpty(list)) {
            LmsOrder order = this.getById(list.get(0).getOrderId());
            return order.getOrderStatus() == 2 || order.getOrderStatus() == 3;
        }
        return false;
    }

    @Override
    public Long fetchOrderCount(String statusStart, String statusEnd, String userSn) {
        QueryWrapper<LmsOrder> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LmsOrder> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(statusStart) && StrUtil.isNotEmpty(statusEnd)) {
            lambda.ge(LmsOrder::getOrderStatus, statusStart);
            lambda.le(LmsOrder::getOrderStatus, statusEnd);
        }
        if (StrUtil.isNotEmpty(userSn)) {
            lambda.eq(LmsOrder::getUserSn, userSn);
        }
        return (long) lmsOrderMapper.selectList(lambda).size();
    }

    @Override
    public boolean updateByUser(Long id, String orderAction, String destination, String attachment,
                                Integer storageDays, String storageLocation) {
        LmsOrder order = this.getById(id);
        if (!StringUtils.isEmpty(orderAction) && (order.getOrderAction().equals("-1")
                || order.getOrderAction().equals("4") || order.getOrderAction().equals("7"))
                && !orderAction.equals(order.getOrderAction())) {
            order.setOrderAction(orderAction);
            if (!orderAction.equals("-1") && !orderAction.equals("0") && !orderAction.equals("4")
                    && !orderAction.equals("7") && !ObjectUtils.isEmpty(order.getWeight())) {
                order.setOrderStatus(0);
            }
        }
        if (!StringUtils.isEmpty(destination) && StringUtils.isEmpty(order.getDestination())
                && (orderAction.equals("0") || orderAction.equals("1") || orderAction.equals("3"))) {
            order.setDestination(destination);
        }
        if (!StringUtils.isEmpty(attachment)) {
            order.setAttachment(attachment);
        }
        if (!ObjectUtils.isEmpty(storageDays)) {
            order.setStorageDays(storageDays);
        }
        if (!StringUtils.isEmpty(storageLocation)) {
            order.setStorageLocation(storageLocation);
        }
        return updateById(order);
    }

    @Override
    public int refreshOrderStatus(LmsOrder order) {
        if (!order.getOrderAction().equals("-1") && !order.getOrderAction().equals("0")
                && !order.getOrderAction().equals("4") && !order.getOrderAction().equals("7")
                && !ObjectUtils.isEmpty(order.getWeight()) && order.getOrderStatus().equals(4)) {
            return 0;
        } else {
            return order.getOrderStatus();
        }
    }

}
