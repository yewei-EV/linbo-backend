package com.ev.linbo.backend.modules.lms.dto;

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
public class LmsOrderCountParam {
    @ApiModelProperty(value = "相差天数")
    private Integer dayOffset;
    @ApiModelProperty(value = "状态")
    private List<Integer> statuses;
}
