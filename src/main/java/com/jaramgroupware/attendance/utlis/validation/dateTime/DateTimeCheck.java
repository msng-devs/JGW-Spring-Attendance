package com.jaramgroupware.attendance.utlis.validation.dateTime;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateTimeValidation.class)
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeCheck {

    String message() default "{start datetime 은 end datetime 보다 나중일 수 없습니다.}";
    Class<?>[] groups() default {};
    String startDateTime();
    String endDateTime();
    Class<? extends Payload>[] payload() default {};
}

