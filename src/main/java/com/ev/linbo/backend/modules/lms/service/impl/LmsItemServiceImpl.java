package com.ev.linbo.backend.modules.lms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ev.linbo.backend.modules.lms.dto.LmsItemQueryParam;
import com.ev.linbo.backend.modules.lms.mapper.LmsOrderMapper;
import com.ev.linbo.backend.modules.lms.model.LmsItem;
import com.ev.linbo.backend.modules.lms.mapper.LmsItemMapper;
import com.ev.linbo.backend.modules.lms.model.LmsOrder;
import com.ev.linbo.backend.modules.lms.model.LmsOrderItemRelation;
import com.ev.linbo.backend.modules.lms.service.LmsItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ev.linbo.backend.modules.lms.service.LmsOrderItemRelationService;
import com.ev.linbo.backend.modules.lms.service.LmsOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (!StringUtils.isEmpty(orderAction) && (item.getItemStatus() == 0 || item.getItemStatus() == 1)) {
            if (orderAction.equals("2") || orderAction.equals("3") || orderAction.equals("5")) {
                item.setItemStatus(3);
            } else if (orderAction.equals("4")) {
                item.setItemStatus(8);
            } else if (item.getItemStatus() == 0) {
                item.setItemStatus(1);
            } else {
                item.setItemStatus(2);
            }
        } else if (item.getItemStatus() == 2) {
            switch (orderAction) {
                case "0":
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
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "8":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(20);
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
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    case "5":
                        if (lmsOrderService.checkIfPaid(item.getId())) {
                            item.setItemStatus(9);
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    case "8":
                        if (lmsOrderService.checkIfPaid(item.getId())) {
                            item.setItemStatus(20);
                        } else {
                            item.setItemStatus(3);
                        }
                        break;
                    default:
                        item.setItemStatus(3);
                }
        } else if (item.getItemStatus() == 4) {
            item.setItemStatus(21);
        } else if (item.getItemStatus() == 5 || item.getItemStatus() == 6
                || item.getItemStatus() == 7 || item.getItemStatus() == 9 || item.getItemStatus() == 20) {
            item.setItemStatus(10);
            lmsOrderService.finishOrder(item.getId());
        } else if (item.getItemStatus() == 8) {
            item.setItemStatus(11);
        } else if (item.getItemStatus() == 21) {
            item.setItemStatus(12);
        } else if (item.getItemStatus() == 11) {
            // 已海外寄存
            switch (orderAction) {
                case "0":
                    item.setItemStatus(4);
                    break;
                case "1":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(5);
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "3":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(7);
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "5":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(9);
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                case "8":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(20);
                    } else {
                        item.setItemStatus(3);
                    }
                    break;
                default:
                    item.setItemStatus(11);
            }
        } else if (item.getItemStatus() == 12) {
            switch (orderAction) {
                case "6":
                    if (lmsOrderService.checkIfPaid(item.getId()) || item.getLocation().equals("CN")) {
                        item.setItemStatus(13);
                    } else {
                        item.setItemStatus(12);
                    }
                    break;
                case "7":
                    item.setItemStatus(15);
                    break;
                case "9":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(14);
                    } else {
                        item.setItemStatus(12);
                    }
                    break;
                default:
                    item.setItemStatus(12);
            }
        } else if (item.getItemStatus() == 13) {
            item.setItemStatus(22);
        } else if (item.getItemStatus() == 14) {
            item.setItemStatus(16);
            lmsOrderService.finishOrder(item.getId());
        } else if (item.getItemStatus() == 15) {
            item.setItemStatus(17);
        } else if (item.getItemStatus() == 17) {
            // 已国内寄存
            switch (orderAction) {
                case "6":
                    if (lmsOrderService.checkIfPaid(item.getId()) || item.getLocation().equals("CN")) {
                        item.setItemStatus(13);
                    } else {
                        item.setItemStatus(12);
                    }
                    break;
                case "9":
                    if (lmsOrderService.checkIfPaid(item.getId())) {
                        item.setItemStatus(14);
                    } else {
                        item.setItemStatus(12);
                    }
                    break;
            }
        } else if (item.getItemStatus() == 22) {
            item.setItemStatus(23);
        } else if (item.getItemStatus() == 23) {
            item.setItemStatus(24);

        }
        return this.updateById(item)?"成功":"失败";
    }

    @Override
    public Page<LmsItem> list(LmsItemQueryParam lmsItemQueryParam) {
        Page<LmsItem> page = new Page<>(lmsItemQueryParam.getPageNum(), lmsItemQueryParam.getPageSize());
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        wrapper.isNotNull("delivery_sn");
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();
        lambda.eq(LmsItem::isArchived, false);
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getDeliverySn())){
            lambda.like(LmsItem::getDeliverySn, lmsItemQueryParam.getDeliverySn());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getUserSn())){
            lambda.eq(LmsItem::getUserSn, lmsItemQueryParam.getUserSn());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getLocation())){
            lambda.eq(LmsItem::getLocation, lmsItemQueryParam.getLocation());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getNote())){
            lambda.like(LmsItem::getNote, lmsItemQueryParam.getNote());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getCreateTime())){
            lambda.like(LmsItem::getCreateTime, lmsItemQueryParam.getCreateTime());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getSku())){
            lambda.eq(LmsItem::getSku, lmsItemQueryParam.getSku());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getSize())){
            lambda.eq(LmsItem::getSize, lmsItemQueryParam.getSize());
        }
        if (!CollectionUtils.isEmpty(lmsItemQueryParam.getItemStatuses())) {
            lambda.in(LmsItem::getItemStatus, lmsItemQueryParam.getItemStatuses());
        } else if (lmsItemQueryParam.getItemStatus()!=null){
            lambda.eq(LmsItem::getItemStatus, lmsItemQueryParam.getItemStatus());
        } else if (CollectionUtils.isEmpty(lmsItemQueryParam.getItemStatuses()) && lmsItemQueryParam.getItemStatus()==null
                && !StringUtils.isEmpty(lmsItemQueryParam.getRequestBy())) {
            if (lmsItemQueryParam.getRequestBy().equals("CN")) {
                lambda.in(LmsItem::getItemStatus, Arrays.asList(21,12,13,14,15,16,17,18,22,23,24));
            } else {
                lambda.in(LmsItem::getItemStatus, Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,20,25));
            }
        }

        if (StrUtil.isNotEmpty(lmsItemQueryParam.getPositionInfo())) {
            lambda.like(LmsItem::getPositionInfo, lmsItemQueryParam.getPositionInfo());
        }
        if (StrUtil.isNotEmpty(lmsItemQueryParam.getRemark())){
            lambda.like(LmsItem::getRemark, lmsItemQueryParam.getRemark());
        }
        return page(page,wrapper);
    }

    @Override
    public Boolean checkIfExist(String deliverySn, String userSn, String location) {
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();
        lambda.eq(LmsItem::isArchived, false);

        if (StrUtil.isNotEmpty(deliverySn)) {
            lambda.eq(LmsItem::getDeliverySn, deliverySn);
        }
        if (StrUtil.isNotEmpty(userSn)) {
            lambda.like(LmsItem::getUserSn, userSn);
        }
        if (StrUtil.isNotEmpty(location)) {
            lambda.like(LmsItem::getLocation, location);
        }
        return lmsItemMapper.selectList(lambda).size() > 0;
    }

    @Override
    public List<LmsOrder> getOrderList(Long itemId) {
        return lmsOrderMapper.getOrderList(itemId);
    }

    @Override
    public boolean allocateOrder(Long itemId, Long orderId) {
        QueryWrapper<LmsOrderItemRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(LmsOrderItemRelation::getItemId, itemId);
        //删除原来的订单
        List<LmsOrderItemRelation> relationList = lmsOrderItemRelationService.list(wrapper);
        List<Long> orderList = new ArrayList<>();
        for (LmsOrderItemRelation relation: relationList) {
            if (!relation.getOrderId().equals(orderId)) {
                orderList.add(relation.getOrderId());
            }
        }
        lmsOrderService.delete(orderList);
        //删除原来的关系
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
    public Long fetchItemCount(String location, String date, List<Integer> statuses, String userSn) {
        QueryWrapper<LmsItem> wrapper = new QueryWrapper<>();
        LambdaQueryWrapper<LmsItem> lambda = wrapper.lambda();
        lambda.eq(LmsItem::isArchived, false);
        if (StrUtil.isNotEmpty(location)) {
            lambda.eq(LmsItem::getLocation, location);
        }
        if (StrUtil.isNotEmpty(date)) {
            lambda.like(LmsItem::getCreateTime, date);
        }
        if (!CollectionUtils.isEmpty(statuses)) {
            lambda.in(LmsItem::getItemStatus, statuses);
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

    @Override
    public void refreshItemStatus(LmsItem item, String orderAction) {
        switch (orderAction) {
            case "0":
                item.setItemStatus(4);
                break;
            case "1":
                if (lmsOrderService.checkIfPaid(item.getId())) {
                    item.setItemStatus(5);
                } else {
                    item.setItemStatus(2);
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
                } else {
                    item.setItemStatus(3);
                }
                break;
            case "6":
                if (lmsOrderService.checkIfPaid(item.getId()) || item.getLocation().equals("CN")) {
                    item.setItemStatus(13);
                } else {
                    if (!item.getItemStatus().equals(13) && !item.getItemStatus().equals(22)
                            && !item.getItemStatus().equals(23) && !item.getItemStatus().equals(24)) {
                        item.setItemStatus(12);
                    }
                }
                break;
            case "7":
                item.setItemStatus(15);
                break;
            case "8":
                if (lmsOrderService.checkIfPaid(item.getId())) {
                    item.setItemStatus(20);
                } else {
                    item.setItemStatus(2);
                }
                break;
            case "9":
                if (item.getItemStatus().equals(16)) {
                    break;
                }
                if (lmsOrderService.checkIfPaid(item.getId())) {
                    item.setItemStatus(14);
                } else {
                    item.setItemStatus(12);
                }
                break;
        }
        this.updateById(item);
    }

}
