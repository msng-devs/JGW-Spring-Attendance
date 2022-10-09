package com.jaramgroupware.attendance.utils.validation.member;

import com.jaramgroupware.attendance.dto.member.controllerDto.MemberBulkUpdateRequestControllerDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;


public class BulkUpdateMemberValidator implements ConstraintValidator<BulkUpdateMemberValid, Set<MemberBulkUpdateRequestControllerDto>> {

    @Override
    public boolean isValid(Set<MemberBulkUpdateRequestControllerDto> memberUpdateRequestServiceDto, ConstraintValidatorContext context) {

        return memberUpdateRequestServiceDto.stream().map(MemberBulkUpdateRequestControllerDto::getId).collect(Collectors.toSet()).size()
                == memberUpdateRequestServiceDto.size();
    }
}
