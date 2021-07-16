package com.macro.mall.tiny.modules.lms.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 货物表
 * </p>
 *
 * @author yewei
 * @since 2021-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("lms_item")
@ApiModel(value="LmsItem对象", description="货物表")
public class LmsItem implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "运单号")
    private String deliverySn;

    @ApiModelProperty(value = "用户识别码")
    private String userSn;

    @ApiModelProperty(value = "货物地点")
    private String location;

    @ApiModelProperty(value = "物流单号")
    private String note;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "尺寸")
    private String size;

    @ApiModelProperty(value = "货物状态：0->未入库；1->已入库")
    private Integer itemStatus;

    @ApiModelProperty(value = "位置信息")
    private String positionInfo;

    @ApiModelProperty(value = "照片")
    private String photo;

    @ApiModelProperty(value = "备注")
    private String remark;

}
