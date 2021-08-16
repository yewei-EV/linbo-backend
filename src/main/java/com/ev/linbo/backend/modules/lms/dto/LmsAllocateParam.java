package com.ev.linbo.backend.modules.lms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * 用户登录参数
 * Created by macro on 2018/4/26.
 */
@Getter
@Setter
public class LmsAllocateParam {
    @NotEmpty
    @ApiModelProperty(value = "货物ID")
    private Long itemId;
    @NotEmpty
    @ApiModelProperty(value = "订单ID")
    private Long orderId;
}
