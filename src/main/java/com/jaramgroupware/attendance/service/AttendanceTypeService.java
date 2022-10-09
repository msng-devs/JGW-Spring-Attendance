package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceTypeRepository;
import com.jaramgroupware.attendance.dto.attendanceType.serviceDto.AttendanceTypeResponseServiceDto;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AttendanceTypeService {

    private final AttendanceTypeRepository attendanceTypeRepository;

    @Transactional(readOnly = true)
    public AttendanceTypeResponseServiceDto findById(Integer id){
        AttendanceType targetAttendanceType = attendanceTypeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ATTENDANCE_TYPE_ID));
        return new AttendanceTypeResponseServiceDto(targetAttendanceType);
    }

    @Transactional(readOnly = true)
    public List<AttendanceTypeResponseServiceDto> findAll(){

        return attendanceTypeRepository.findAllBy()
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_TIMETABLE))
                .stream()
                .map(AttendanceTypeResponseServiceDto::new)
                .collect(Collectors.toList());
    }
}
