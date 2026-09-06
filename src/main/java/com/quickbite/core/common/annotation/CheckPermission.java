package com.quickbite.core.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckPermission {
    String resource();
    String action();
    boolean allowSystemAdmin() default true;
}
