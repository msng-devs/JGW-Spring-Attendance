package com.jaramgroupware.attendance.utils.validation.attendance;

import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceAddRequestControllerDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;


public class BulkAddAttendanceValidator implements ConstraintValidator<BulkAddAttendanceValid, Set<AttendanceAddRequestControllerDto>> {



    @Override
    public boolean isValid(Set<AttendanceAddRequestControllerDto> value, ConstraintValidatorContext context) {
        return value.stream().map(AttendanceAddRequestControllerDto::toId).collect(Collectors.toSet()).size()
                == value.size();
    }
}
