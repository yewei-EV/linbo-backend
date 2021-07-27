package com.macro.mall.tiny.modules.lms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.macro.mall.tiny.modules.lms.dto.LmsItemQueryParam;
import com.macro.mall.tiny.modules.lms.model.LmsItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.lms.model.LmsOrder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 货物表 服务类
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
public interface LmsItemService extends IService<LmsItem> {
    /**
     * 添加货物
     */
    boolean create(LmsItem item);

    /**
     * 批量删除货物
     */
    boolean delete(List<Long> ids);

    /**
     * 修改货物状态
     */
    String updateItemStatus(LmsItem item, String orderAction);

    /**
     * 分页获取货物列表
     */
    Page<LmsItem> list(LmsItemQueryParam lmsItemQueryParam);

    /**
     * 查询货物是否存在
     */
    Boolean checkIfExist(String deliverySn, String userSn, String location);

    /**
     * 获取货物对应订单
     */
    List<LmsOrder> getOrderList(Long itemId);

    /**
     * 修改货物与订单关系
     */
    @Transactional
    boolean allocateOrder(Long itemId, Long orderId);

    /**
     * 获取货物统计
     */
    Long fetchItemCount(String location, String date, List<Integer> statuses, String userSn);

    /**
     * 修改货物状态
     */
    Boolean modifyItemStatus(List<Long> ids, String newStatus);

    /**
     * 获取货物对应订单
     */
    List<LmsItem> getItemListByOrder(Long orderId);

    /**
     * 刷新货物状态
     */
    void refreshItemStatus(LmsItem item, String orderAction);
}
