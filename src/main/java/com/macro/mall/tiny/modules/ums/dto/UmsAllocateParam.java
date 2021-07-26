package com.macro.mall.tiny.modules.ums.dto;

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
public class UmsAllocateParam {
    @NotEmpty
    @ApiModelProperty(value = "用户ID")
    private Long adminId;
    @NotEmpty
    @ApiModelProperty(value = "地址ID")
    private Long addressId;
}
