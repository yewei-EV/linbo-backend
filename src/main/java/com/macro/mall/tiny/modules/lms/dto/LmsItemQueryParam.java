package com.macro.mall.tiny.modules.lms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户登录参数
 * Created by macro on 2018/4/26.
 */
@Getter
@Setter
public class LmsItemQueryParam {
    @ApiModelProperty(value = "货物ID")
    private Long itemId;
    @ApiModelProperty(value = "订单ID")
    private Long orderId;
    @ApiModelProperty(value = "运单号")
    private String deliverySn;
    @ApiModelProperty(value = "识别码")
    private String userSn;
    @ApiModelProperty(value = "位置")
    private String location;
    @ApiModelProperty(value = "物流单号")
    private String note;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "SKU")
    private String sku;
    @ApiModelProperty(value = "尺寸")
    private String size;
    @ApiModelProperty(value = "包裹状态")
    private Integer itemStatus;
    @ApiModelProperty(value = "包裹多种状态")
    private List<Integer> itemStatuses;
    @ApiModelProperty(value = "存储位置")
    private String positionInfo;
    @ApiModelProperty(value = "每页尺寸")
    private Integer pageSize;
    @ApiModelProperty(value = "页码")
    private Integer pageNum;
}
