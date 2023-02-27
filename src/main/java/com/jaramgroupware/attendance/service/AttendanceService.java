package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.attendance.AttendanceRepository;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceTypeRepository;
import com.jaramgroupware.attendance.domain.member.MemberRepository;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableRepository;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceResponseServiceDto;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceErrorCode;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final TimeTableRepository timeTableRepository;
    private final AttendanceTypeRepository attendanceTypeRepository;

    @Transactional
    public AttendanceResponseServiceDto createAttendance(AttendanceAddRequestServiceDto requestDto) {
        var targetMember = memberRepository.findById(requestDto.getMemberId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_MEMBER));
        var targetTimeTable = timeTableRepository.findById(requestDto.getTimeTableId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_TIMETABLE));
        var targetAttendanceType = attendanceTypeRepository.findById(requestDto.getAttendanceTypeId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE_TYPE));

        var newAttendance = requestDto.toEntity(targetMember, targetAttendanceType, targetTimeTable);

        return new AttendanceResponseServiceDto(attendanceRepository.save(newAttendance));
    }

    @Transactional(readOnly = true)
    public AttendanceResponseServiceDto findAttendanceById(Long id) {
        var targetAttendance = attendanceRepository.findById(id).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE));

        return new AttendanceResponseServiceDto(targetAttendance);
    }

    @Transactional
    public AttendanceResponseServiceDto updateAttendance(AttendanceUpdateRequestServiceDto requestDto){
        var targetAttendance = attendanceRepository.findById(requestDto.getAttendanceId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE));
        if(!targetAttendance.getAttendanceType().getId().equals(requestDto.getAttendanceTypeId())){
            var targetAttendanceType = attendanceTypeRepository.findById(requestDto.getAttendanceTypeId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE_TYPE));
            var updateAttendance = requestDto.toEntity(targetAttendanceType);
            targetAttendance.update(updateAttendance);
        }
        else{
            var updateAttendance = requestDto.toEntity();
            targetAttendance.update(updateAttendance);
        }

        return new AttendanceResponseServiceDto(attendanceRepository.save(targetAttendance));

    }

    @Transactional
    public void deleteAttendance(Long attendanceId){
        attendanceRepository.deleteAttendanceById(attendanceId);
    }
}
