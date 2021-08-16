package com.ev.linbo.backend.security.annotation;

import java.lang.annotation.*;

/**
 * 定义操作日志注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {

    String operation() default "";

    String operateType() default "";

    String module() default "";

}
