package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.attendanceType.AttendanceTypeRepository;
import com.jaramgroupware.attendance.dto.attendanceType.serviceDto.AttendanceTypeResponseServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AttendanceTypeService {
    private final AttendanceTypeRepository attendanceTypeRepository;

    public List<AttendanceTypeResponseServiceDto> findAll(){
        return attendanceTypeRepository.findAll().stream().map(AttendanceTypeResponseServiceDto::new).collect(Collectors.toList());
    }
}
