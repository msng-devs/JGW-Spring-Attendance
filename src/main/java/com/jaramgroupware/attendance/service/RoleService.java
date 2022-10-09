package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.role.Role;
import com.jaramgroupware.attendance.domain.role.RoleRepository;
import com.jaramgroupware.attendance.dto.role.serviceDto.RoleResponseServiceDto;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public RoleResponseServiceDto findById(Integer id){
        Role targetRole = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ROLE_ID));
        return new RoleResponseServiceDto(targetRole);
    }

    @Transactional(readOnly = true)
    public List<RoleResponseServiceDto> findAll(){
        return roleRepository.findAllBy()
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_ROLE))
                .stream()
                .map(RoleResponseServiceDto::new)
                .collect(Collectors.toList());
    }

}
