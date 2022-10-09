package com.jaramgroupware.attendance.utils.validation.attendance;

import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceBulkUpdateRequestControllerDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;


public class BulkUpdateAttendanceValidator implements ConstraintValidator<BulkUpdateAttendanceValid, Set<AttendanceBulkUpdateRequestControllerDto>> {

    @Override
    public boolean isValid(Set<AttendanceBulkUpdateRequestControllerDto> dtos, ConstraintValidatorContext context) {

        return dtos.stream().map(AttendanceBulkUpdateRequestControllerDto::toId).collect(Collectors.toSet()).size()
                == dtos.size();
    }
}
