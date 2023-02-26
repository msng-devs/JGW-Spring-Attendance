package com.jaramgroupware.attendance.web.aop.authorization;

import java.lang.annotation.*;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RBAC {
    int role() default 1;
}
