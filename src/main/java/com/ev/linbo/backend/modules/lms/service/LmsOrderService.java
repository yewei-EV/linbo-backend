package com.ev.linbo.backend.modules.lms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ev.linbo.backend.modules.lms.model.LmsOrder;

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
                        String location, String createTime, Integer status, Integer paymentStatus, String paymentTime,
                        String newDeliverySn, String updateTime, Integer pageSize, Integer pageNum);

    /**
     * 获取货物统计
     */
    Float fetchOrderPriceCount(String location, String date);

    /**
     * 获取已支付订单
     */
    boolean checkIfPaid(Long itemId);

    /**
     * 完成订单
     */
    void finishOrder(Long itemId);

    /**
     * 获取订单数量
     */
    Long fetchOrderCount(String date, List<Integer> statuses);

    /**
     * 用户修改订单
     */
    boolean updateByUser(Long id, String orderAction, String destination, String attachment, Integer storageDays,
                         String storageLocation, String overtimeDate, String labelNumber, String userRemark,
                         String chinaSize, Boolean isFollowPrice);

    /**
     * 刷新订单状态
     * @return
     */
    int refreshOrderStatus(LmsOrder order);

    void separateOrders(LmsOrder order);
}
