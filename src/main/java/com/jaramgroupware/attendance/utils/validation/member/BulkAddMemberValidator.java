package com.jaramgroupware.attendance.utils.validation.member;

import com.jaramgroupware.attendance.dto.member.controllerDto.MemberAddRequestControllerDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;


public class BulkAddMemberValidator implements ConstraintValidator<BulkAddMemberValid, Set<MemberAddRequestControllerDto>> {

    @Override
    public boolean isValid(Set<MemberAddRequestControllerDto> dtos, ConstraintValidatorContext context) {

        return dtos.stream().map(MemberAddRequestControllerDto::getId).collect(Collectors.toSet()).size()
                == dtos.size();
    }
}
