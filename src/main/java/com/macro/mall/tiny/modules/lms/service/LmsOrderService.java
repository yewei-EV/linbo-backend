package com.macro.mall.tiny.modules.lms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
public interface LmsOrderService extends IService<LmsOrder> {
    /**
     * 添加订单
     */
    boolean create(LmsOrder order);

    /**
     * 批量删除订单
     */
    boolean delete(List<Long> ids);

    /**
     * 分页获取订单列表
     */
    Page<LmsOrder> list(Long id, String action, String deliverySn, String userSn, String destination, String note,
                        String createTime, Integer status, Integer paymentStatus, String paymentTime, Integer pageSize,
                        Integer pageNum);

    /**
     * 根据订单状态来更新货物状态
     */
    boolean refreshItemsStatusByOrder(Long orderId, LmsOrder order);

    /**
     * 获取货物统计
     */
    Float fetchOrderPriceCount(String location, String date);

}
