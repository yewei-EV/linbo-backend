package com.macro.mall.tiny.modules.lms.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@EqualsAndHashCode(callSuper = false)
@TableName("lms_order")
@ApiModel(value="LmsOrder对象", description="订单表")
public class LmsOrder implements Serializable {

    private static final long serialVersionUID=1L;

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

}
