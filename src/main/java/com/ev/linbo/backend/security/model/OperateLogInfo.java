package com.ev.linbo.backend.security.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("operate_log")
@Data
public class OperateLogInfo {

    //主键id
    @TableId
    private String id;
    //操作人名称
    private String userName;
    //操作内容
    private String operation;
    //操作方法名称
    private String method;
    //操作后的数据
    private String modifiedData;
    //操作前数据
    private String preModifiedData;
    //操作是否成功
    private String result;
    //报错信息
    private String errorMessage;
    //报错堆栈信息
    private String errorStackTrace;
    //开始执行时间
    private Date executeTime;
    //执行持续时间
    private Long duration;
    //ip
    private String ip;
    //操作模块
    private String module;
    //操作类型
    private String operateType;
}
