package com.jaramgroupware.attendance.utlis.validation.pageAble;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Pageable;


import java.util.Arrays;
import java.util.List;

public class PageableValidator implements ConstraintValidator<PageableValid, Pageable> {

    private List<String> sortKeys;
    @Override
    public void initialize(PageableValid constraintAnnotation) {
        sortKeys = Arrays.asList(constraintAnnotation.sortKeys());
    }
    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {

        return value.getSort().stream().allMatch(sort -> sortKeys.contains(sort.getProperty()));
    }
}
