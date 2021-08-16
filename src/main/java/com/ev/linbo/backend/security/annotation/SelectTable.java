package com.ev.linbo.backend.security.annotation;

import java.lang.annotation.*;

/**
 * 定义用于找到表和表主键的注解
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectTable {

    String tableName() default "";

    String idName() default  "";

}
