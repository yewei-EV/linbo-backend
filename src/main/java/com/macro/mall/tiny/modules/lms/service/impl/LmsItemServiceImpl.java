package com.macro.mall.tiny.modules.lms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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

    private final LmsItemMapper lmsItemMapper;

    private final LmsOrderItemRelationService lmsOrderItemRelationService;

    private final LmsOrderService lmsOrderService;

    public LmsItemServiceImpl(LmsOrderMapper lmsOrderMapper, LmsItemMapper lmsItemMapper, LmsOrderItemRelationService lmsOrderItemRelationService, LmsOrderService lmsOrderService) {
        this.lmsOrderMapper = lmsOrderMapper;
        this.lmsItemMapper = lmsItemMapper;
        this.lmsOrderItemRelationService = lmsOrderItemRelationService;
        this.lmsOrderService = lmsOrderService;
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
            wrapper.lambda().eq(LmsOrderItemRelation::getItemId, id);
            List<LmsOrderItemRelation> list = lmsOrderItemRelationService.list(wrapper);
            for (LmsOrderItemRelation relation: list) {
                if (!ObjectUtils.isEmpty(lmsOrderService.getById(relation.getOrderId()))) {
                    lmsOrderMapper.deleteById(relation.getOrderId());
                }
            }
            lmsOrderItemRelationService.remove(wrapper);
        }
        return removeByIds(ids);
    }

    @Override
    public String updateItemStatus(LmsItem item, String orderAction) {
        if (!StringUtils.isEmpty(orderAction) && (item.getItemStatus() == 1 || item.getItemStatus() == 0)) {
            item.setItemStatus(2);
        } else if (item.getItemStatus() == 2) {
            switch (orderAction) {
                case "0":
                case "6":
                case "7":
                    item.setItemStatus(4);
                    break;
                case "1":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(5);
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "2":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(6);
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "3":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(7);
                        break;
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "4":
                    item.setItemStatus(8);
                    break;
                case "5":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(9);
                        break;
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                default:
                    item.setItemStatus(3);
            }
        } else if (item.getItemStatus() == 3) {
                switch (orderAction) {
                    case "1":
                        if (lmsOrderService.checkIfPaid(item.getId())) {
                            item.setItemStatus(5);
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    case "2":
                        if (lmsOrderService.checkIfPaid(item.getId())) {
                            item.setItemStatus(6);
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    case "3":
                        if (lmsOrderService.checkIfPaid(item.getId())) {
                            item.setItemStatus(7);
                            break;
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    case "5":
                        if (lmsOrderService.checkIfPaid(item.getId())) {
                            item.setItemStatus(9);
                            break;
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    default:
                        item.setItemStatus(3);
                }
        } else if (item.getItemStatus() == 4 || item.getItemStatus() == 5 || item.getItemStatus() == 6
                || item.getItemStatus() == 7 || item.getItemStatus() == 9) {
            item.setItemStatus(10);
        } else if (item.getItemStatus() == 8) {
            item.setItemStatus(11);
        } else if (item.getItemStatus() == 10) {
            item.setItemStatus(12);
        } else if (item.getItemStatus() == 12) {
            switch (orderAction) {
                case "0":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(14);
                        break;
                    } else {
                        return "未付款";
                    }
                case "6":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(13);
                        break;
                    } else {
                        return "未付款";
                    }
                case "7":
                    item.setItemStatus(15);
                    break;
                default:
                    item.setItemStatus(12);
            }
        } else if (item.getItemStatus() == 13 || item.getItemStatus() == 14) {
            if (lmsOrderService.checkIfPaid(item.getId())) {
                item.setItemStatus(16);
            } else {
                return "未付款";
            }
        } else if (item.getItemStatus() == 15) {
            item.setItemStatus(17);
        }
        return this.updateById(item)?"成功":"失败";
    }

    @Override
    public Page<LmsItem> list(String deliverySn, String userSn, String location, String note, String createTime,
                              String sku, String size, Integer itemStatus, String positionInfo, Integer pageSize,
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
        if(itemStatus!=null){
            lambda.eq(LmsItem::getItemStatus, itemStatus);
        }
        if(StrUtil.isNotEmpty(positionInfo)){
            lambda.like(LmsItem::getPositionInfo, positionInfo);
        }
        return page(page,wrapper);
    }

    @Override
    public Page<LmsItem> listPrecise(String deliverySn, String userSn, String location, String note, String createTime,
                              String sku, String size, Integer itemStatus, String positionInfo, Integer pageSize,
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
        if(itemStatus!=null){
            lambda.eq(LmsItem::getItemStatus, itemStatus);
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
        wrapper.lambda().eq(LmsOrderItemRelation::getItemId, itemId);
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

    @Override
    public Long fetchItemCount(String location, String date, String statusStart, String statusEnd, String userSn) {
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();
        if (StrUtil.isNotEmpty(location)) {
            lambda.eq(LmsItem::getLocation, location);
        }
        if (StrUtil.isNotEmpty(date)) {
            lambda.like(LmsItem::getCreateTime, date);
        }
        if (StrUtil.isNotEmpty(statusStart) && StrUtil.isNotEmpty(statusEnd)) {
            lambda.ge(LmsItem::getItemStatus, statusStart);
            lambda.le(LmsItem::getItemStatus, statusEnd);
        }
        if (StrUtil.isNotEmpty(userSn)) {
            lambda.eq(LmsItem::getUserSn, userSn);
        }
        return (long) lmsItemMapper.selectList(lambda).size();
    }

    @Override
    public Boolean modifyItemStatus(List<Long> ids, String newStatus) {
        for (Long id : ids) {
            LambdaUpdateWrapper<LmsItem> itemUpdateWrapper = new LambdaUpdateWrapper<>();
            itemUpdateWrapper.eq(LmsItem::getId, id)
                    .set(LmsItem::getItemStatus, newStatus);
            this.update(itemUpdateWrapper);
        }
        return true;
    }

    @Override
    public List<LmsItem> getItemListByOrder(Long orderId) {
        QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LmsOrderItemRelation::getOrderId, orderId);
        List<LmsOrderItemRelation> list = lmsOrderItemRelationService.list(wrapper);
        List<LmsItem> itemList = new ArrayList<>();
        for (LmsOrderItemRelation relation: list) {
            itemList.add(this.getById(relation.getItemId()));
        }
        return itemList;
    }

}
