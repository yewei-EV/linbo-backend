package com.ev.linbo.backend.security.annotation;

import java.lang.annotation.*;

/**
 * 定义获取主键值的注解
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectPrimaryKey {

}
