package com.ev.linbo.backend.modules.lms.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.ev.linbo.backend.security.annotation.SelectPrimaryKey;
import com.ev.linbo.backend.security.annotation.SelectTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
@Data
@SelectTable(tableName = "lms_order", idName = "id")
@EqualsAndHashCode(callSuper = false)
@TableName("lms_order")
@ApiModel(value="LmsOrder对象", description="订单表")
public class LmsOrder implements Serializable {

    public static LmsOrder copy(LmsOrder order) {
        LmsOrder newOrder = new LmsOrder();
        newOrder.archived = order.isArchived();
        newOrder.orderAction = order.getOrderAction();
        newOrder.weight = order.getWeight();
        newOrder.weightUnit = order.getWeightUnit();
        newOrder.location = order.getLocation();
        newOrder.amount = 1;
        newOrder.deliverySn = order.getDeliverySn();
        newOrder.userSn = order.getUserSn();
        newOrder.destination = order.getDestination();
        newOrder.note = order.getNote();
        newOrder.createTime = order.getCreateTime();
        newOrder.updateTime = new Date();
        newOrder.orderStatus = order.getOrderStatus();
        newOrder.paymentStatus = order.getPaymentStatus();
        newOrder.paymentTime = order.getPaymentTime();
        newOrder.price = order.getPrice();
        newOrder.newDeliverySn = order.getNewDeliverySn();
        newOrder.attachment = order.getAttachment();
        newOrder.storageStartTime = order.getStorageStartTime();
        newOrder.storageDays = order.getStorageDays();
        newOrder.storageLocation = order.getStorageLocation();
        newOrder.overtimeDate = order.getOvertimeDate();
        newOrder.labelNumber = order.getLabelNumber();
        newOrder.userRemark = order.getUserRemark();
        newOrder.sfPrice = order.getSfPrice();
        newOrder.chinaSize = order.getChinaSize();
        return newOrder;
    }

    private static final long serialVersionUID=1L;

    @SelectPrimaryKey
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "归档")
    boolean archived = false;

    @ApiModelProperty(value = "订单操作")
    private String orderAction;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "重量单位：0->lbs；1->kg")
    private Integer weightUnit;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "货物数量")
    private Integer amount;

    @ApiModelProperty(value = "运单号")
    private String deliverySn;

    @ApiModelProperty(value = "用户识别码")
    private String userSn;

    @ApiModelProperty(value = "到货地址")
    private String destination;

    @ApiModelProperty(value = "备注信息")
    private String note;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "支付状态：0->未定价；1->待支付 2->未支付 3->支付失败 4->支付取消")
    private Integer paymentStatus;

    @ApiModelProperty(value = "支付成功时间")
    private Date paymentTime;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "快递单号")
    private String newDeliverySn;

    @ApiModelProperty(value = "附件")
    private String attachment;

    @ApiModelProperty(value = "寄存开始时间")
    private Date storageStartTime;

    @ApiModelProperty(value = "寄存天数")
    private Integer storageDays;

    @ApiModelProperty(value = "寄存位置")
    private String storageLocation;

    @ApiModelProperty(value = "超时时间")
    private Date overtimeDate;

    @ApiModelProperty(value = "Label单号")
    private String labelNumber;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @ApiModelProperty(value = "顺丰运费")
    private BigDecimal sfPrice;

    @ApiModelProperty(value = "国内尺码")
    private String chinaSize;

    @ApiModelProperty(value = "代卖上线时间")
    private Date onlineDate;

    @ApiModelProperty(value = "卖出时间")
    private Date soldDate;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal soldPrice;

    @ApiModelProperty(value = "技术服务费率")
    private BigDecimal techServiceFeePercentage;

    @ApiModelProperty(value = "技术服务费")
    private BigDecimal techServiceFee;

    @ApiModelProperty(value = "转账手续费")
    private BigDecimal transactionFee;

    @ApiModelProperty(value = "查鉴包")
    private BigDecimal duServiceFee;

    @ApiModelProperty(value = "售后无忧费")
    private BigDecimal afterSaleServiceFee;

    @ApiModelProperty(value = "运费+手续费")
    private BigDecimal totalServiceFee;

    @ApiModelProperty(value = "用户到手价格")
    private BigDecimal userOwnPrice;

    @ApiModelProperty(value = "实际入账价格")
    private BigDecimal realSalePrice;

    @ApiModelProperty(value = "实际技术服务费")
    private BigDecimal realTechServiceFee;

    @ApiModelProperty(value = "实际利润")
    private BigDecimal realProfit;

    @ApiModelProperty(value = "是否跟价")
    private Boolean isFollowPrice;

    @ApiModelProperty(value = "结算时间")
    private Date clearDate;

}
