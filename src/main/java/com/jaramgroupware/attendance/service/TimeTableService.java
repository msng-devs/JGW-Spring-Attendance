package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendance.AttendanceRepository;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceTypeRepository;
import com.jaramgroupware.attendance.domain.event.EventRepository;
import com.jaramgroupware.attendance.domain.member.MemberRepository;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableRepository;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceErrorCode;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final AttendanceTypeRepository attendanceTypeRepository;

    @Transactional(readOnly = true)
    public TimeTableResponseServiceDto findServiceById(Long id){
        TimeTable targetTimeTable = timeTableRepository.findById(id).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_TIMETABLE));

        return new TimeTableResponseServiceDto(targetTimeTable);
    }

    @Transactional
    public TimeTableResponseServiceDto createTimeTable(TimeTableAddRequestServiceDto requestDto){
        var targetEvent = eventRepository.findById(requestDto.getEventID()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_EVENT));
        var newTimeTable = requestDto.toEntity(targetEvent);
        if(!newTimeTable.validationDateTime()) throw new ServiceException(ServiceErrorCode.INVALID_TIMETABLE_DATETIME);
        return new TimeTableResponseServiceDto(timeTableRepository.save(newTimeTable));
    }

    @Transactional
    public TimeTableResponseServiceDto createTimeTable(TimeTableAddRequestServiceDto requestDto, Set<Integer> targetRoles,Set<Integer> targetRanks,Integer defaultAttendanceType){
        //신규 timetable 추가
        var targetEvent = eventRepository.findById(requestDto.getEventID()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_EVENT));
        var newTimeTable = requestDto.toEntity(targetEvent);
        if(!newTimeTable.validationDateTime()) throw new ServiceException(ServiceErrorCode.INVALID_TIMETABLE_DATETIME);
        var addedTimeTable = timeTableRepository.save(newTimeTable);

        //해당 timetable 의 신규 attendance 등록
        var targetMembers = memberRepository.findTargetMember(targetRoles,targetRanks);

        if(targetMembers.size() != 0){
            var targetAttendanceType = attendanceTypeRepository.findById(defaultAttendanceType).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_ATTENDANCE_TYPE));
            var newAttendances = targetMembers.stream().map(member -> member.createSystemAttendance(addedTimeTable,targetAttendanceType)).collect(Collectors.toList());
            attendanceRepository.saveAll(newAttendances);
        }

        return new TimeTableResponseServiceDto(addedTimeTable);
    }

    @Transactional
    public TimeTableResponseServiceDto updateTimeTable(TimeTableUpdateRequestServiceDto requestDto){
        var targetTimeTable = timeTableRepository.findById(requestDto.getId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_TIMETABLE));
        targetTimeTable.update(requestDto.toEntity());
        if(!targetTimeTable.validationDateTime()) throw new ServiceException(ServiceErrorCode.INVALID_TIMETABLE_DATETIME);
        return new TimeTableResponseServiceDto(timeTableRepository.save(targetTimeTable));
    }

    @Transactional
    public TimeTableResponseServiceDto deleteTimeTable(Long id){
        var targetTimeTable = timeTableRepository.findById(id).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_TIMETABLE));
        timeTableRepository.delete(targetTimeTable);
        return new TimeTableResponseServiceDto(timeTableRepository.save(targetTimeTable));
    }
}
