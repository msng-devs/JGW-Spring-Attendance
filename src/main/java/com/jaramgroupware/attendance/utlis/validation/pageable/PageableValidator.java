package com.jaramgroupware.attendance.utlis.validation.pageable;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Pageable;


import java.util.Arrays;
import java.util.List;

public class PageableValidator implements ConstraintValidator<PageableValid, Pageable> {

    private List<String> sortKeys;
    private int maxPageSize;
    @Override
    public void initialize(PageableValid constraintAnnotation) {
        sortKeys = Arrays.asList(constraintAnnotation.sortKeys());
        maxPageSize = constraintAnnotation.maxPageSize();
    }
    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {

        return value.getSort().stream().allMatch(sort -> sortKeys.contains(sort.getProperty())) && value.getPageSize() <= maxPageSize;
    }
}
