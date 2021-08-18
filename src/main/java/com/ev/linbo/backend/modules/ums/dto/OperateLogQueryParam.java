package com.ev.linbo.backend.modules.ums.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 日志参数
 * Created by Yewei Wang
 */
@Getter
@Setter
public class OperateLogQueryParam {
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "操作功能")
    private String operation;
    @ApiModelProperty(value = "操作方法名称")
    private String method;
    @ApiModelProperty(value = "操作后的数据")
    private String modifiedData;
    @ApiModelProperty(value = "操作前数据")
    private String preModifiedData;
    @ApiModelProperty(value = "操作是否成功")
    private String result;
    @ApiModelProperty(value = "ip")
    private String ip;
    @ApiModelProperty(value = "操作类型")
    private String operateType;
    @ApiModelProperty(value = "每页尺寸")
    private Integer pageSize;
    @ApiModelProperty(value = "页码")
    private Integer pageNum;
}
