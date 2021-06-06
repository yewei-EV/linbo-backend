package com.macro.mall.tiny.modules.lms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 分页获取货物列表
     */
    Page<LmsItem> list(String deliverySn, String userSn, String location, String note, String createTime,
                       String sku, String size, Integer status, String positionInfo, Integer pageSize, Integer pageNum);

    /**
     * 分页获取精准货物列表
     */
    Page<LmsItem> listPrecise(String deliverySn, String userSn, String location, String note, String createTime,
                       String sku, String size, Integer status, String positionInfo, Integer pageSize, Integer pageNum);

    /**
     * 获取货物对应订单
     */
    List<LmsOrder> getOrderList(Long itemId);

    /**
     * 修改货物与订单关系
     */
    @Transactional
    boolean allocateOrder(Long itemId, Long orderId);

}
