package com.ev.linbo.backend.modules.lms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ev.linbo.backend.modules.lms.mapper.LmsOrderMapper;
import com.ev.linbo.backend.modules.lms.model.LmsOrder;
import com.ev.linbo.backend.modules.lms.model.LmsOrderItemRelation;
import com.ev.linbo.backend.modules.lms.service.LmsOrderItemRelationService;
import com.ev.linbo.backend.modules.lms.service.LmsOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        return save(order);
    }

    @Override
    public boolean delete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public Page<LmsOrder> list(Long id, String orderAction, String deliverySn, String userSn, String destination,
                               String note, String location, String createTime, Integer orderStatus, Integer paymentStatus,
                               String paymentTime, String updateTime, Integer pageSize, Integer pageNum) {
        Page<LmsOrder> page = new Page<>(pageNum,pageSize);
        QueryWrapper<LmsOrder> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        LambdaQueryWrapper<LmsOrder> lambda = wrapper.lambda();
        lambda.eq(LmsOrder::isArchived, false);
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
        if(StrUtil.isNotEmpty(location)){
            lambda.eq(LmsOrder::getLocation, location);
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
        if(StrUtil.isNotEmpty(updateTime)){
            lambda.like(LmsOrder::getUpdateTime, updateTime);
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
    public void finishOrder(Long itemId) {
        QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LmsOrderItemRelation::getItemId, itemId);
        List<LmsOrderItemRelation> list = lmsOrderItemRelationService.list(wrapper);
        if (!CollectionUtils.isEmpty(list)) {
            LmsOrder order = this.getById(list.get(0).getOrderId());
            if (order.getOrderStatus().equals(2)) {
                order.setOrderStatus(3);
                updateById(order);
            }
        }
    }

    @Override
    public Long fetchOrderCount(String date, List<Integer> statuses) {
        QueryWrapper<LmsOrder> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LmsOrder> lambda = wrapper.lambda();
        lambda.eq(LmsOrder::isArchived, false);
        if (!CollectionUtils.isEmpty(statuses)) {
            lambda.in(LmsOrder::getOrderStatus, statuses);
        }
        if (StrUtil.isNotEmpty(date)) {
            lambda.like(LmsOrder::getCreateTime, date);
        }
        return (long) lmsOrderMapper.selectList(lambda).size();
    }

    @Override
    public boolean updateByUser(Long id, String orderAction, String destination, String attachment,
                                Integer storageDays, String storageLocation, String overtimeDate, String labelNumber,
                                String userRemark, String chinaSize, Boolean isFollowPrice) {
        LmsOrder order = this.getById(id);
        if (!StringUtils.isEmpty(orderAction) && (order.getOrderAction().equals("-1")
                || order.getOrderAction().equals("4") || order.getOrderAction().equals("7"))
                && !orderAction.equals(order.getOrderAction())) {
            order.setOrderAction(orderAction);
            if (!orderAction.equals("-1") && !orderAction.equals("0") && !orderAction.equals("4")
                    && !orderAction.equals("6") && !orderAction.equals("7") && !ObjectUtils.isEmpty(order.getWeight())
                    && order.getOrderStatus().equals(4)) {
                //待定价
                order.setOrderStatus(0);
            } else if (orderAction.equals("2") || orderAction.equals("3") || orderAction.equals("5")) {
                //待支付
                switch (order.getLocation()) {
                    case "US1":
                    case "US2":
                    case "JP":
                        order.setPrice(BigDecimal.valueOf(28));
                        order.setOrderStatus(1);
                        break;
                    case "CA":
                        order.setPrice(BigDecimal.valueOf(30));
                        order.setOrderStatus(1);
                        break;
                    case "DE":
                    case "NL":
                    case "SP":
                    case "IT":
                        order.setPrice(BigDecimal.valueOf(35));
                        order.setOrderStatus(1);
                        break;
                    case "AU":
                    case "HK":
                        order.setPrice(BigDecimal.valueOf(40));
                        order.setOrderStatus(1);
                        break;
                    default:
                        order.setOrderStatus(0);
                        break;
                }
            }
        }
        if (!StringUtils.isEmpty(destination) && StringUtils.isEmpty(order.getDestination())
                && (orderAction.equals("1") || orderAction.equals("3")) || orderAction.equals("9")) {
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
        if (!StringUtils.isEmpty(chinaSize)) {
            order.setChinaSize(chinaSize);
        }
        if (!ObjectUtils.isEmpty(isFollowPrice)) {
            order.setIsFollowPrice(isFollowPrice);
        }
        if (!StringUtils.isEmpty(overtimeDate)) {
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(overtimeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            order.setOvertimeDate(date);
        }
        if (!StringUtils.isEmpty(labelNumber)) {
            order.setLabelNumber(labelNumber);
        }
        if (!StringUtils.isEmpty(userRemark)) {
            order.setUserRemark(userRemark);
        }
        order.setUpdateTime(new Date());
        return updateById(order);
    }

    @Override
    public int refreshOrderStatus(LmsOrder order) {
        if (!order.getOrderAction().equals("-1") && !order.getOrderAction().equals("0")
                && !order.getOrderAction().equals("4") && !order.getOrderAction().equals("7")
                && !ObjectUtils.isEmpty(order.getWeight()) && order.getOrderStatus().equals(4)
                && ObjectUtils.isEmpty(order.getPrice())) {
            return 0;
        } else if (!order.getOrderAction().equals("-1") && !order.getOrderAction().equals("0")
                && !order.getOrderAction().equals("4") && !order.getOrderAction().equals("7")
                && !ObjectUtils.isEmpty(order.getWeight()) && order.getOrderStatus().equals(4)
                && !ObjectUtils.isEmpty(order.getPrice())) {
            return 1;
        } else if (order.getOrderAction().equals("9") && order.getOrderStatus().equals(2)
                && !ObjectUtils.isEmpty(order.getSfPrice())) {
            return 1;
        } else {
            return order.getOrderStatus();
        }
    }

}
