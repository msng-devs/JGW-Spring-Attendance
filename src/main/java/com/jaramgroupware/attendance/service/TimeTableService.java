package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.event.EventRepository;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final EventRepository eventRepository;

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
