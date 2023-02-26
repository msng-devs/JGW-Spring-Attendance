package com.jaramgroupware.attendance.utlis.validation.dateTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DateTimeValidation implements
        ConstraintValidator<DateTimeCheck, Object> {

    private final DateTimeCompare dateTimeCompare;
    private String startDateTime;
    private String endDateTime;

    @Override
    public void initialize(DateTimeCheck constraintAnnotation) {
        this.startDateTime = constraintAnnotation.startDateTime();
        this.endDateTime = constraintAnnotation.endDateTime();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Object startDateTimeValue = new BeanWrapperImpl(object).getPropertyValue(startDateTime);
        Object endDateTimeValue = new BeanWrapperImpl(object).getPropertyValue(endDateTime);
        return dateTimeCompare.compare((LocalDateTime) startDateTimeValue,(LocalDateTime) endDateTimeValue);
    }


}
