package com.insu.house.aop;

import java.lang.annotation.*;

/**
 * 用户Controller注解
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemControllerAnnotation {
    String description() default "";
}
