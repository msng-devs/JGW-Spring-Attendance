package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableRepository;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableResponseServiceDto;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utils.exception.CustomException;
import com.jaramgroupware.attendance.utils.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TimeTableService {

    @Autowired
    private final TimeTableRepository timeTableRepository;

    @Transactional(readOnly = true)
    public TimeTableResponseServiceDto findById(Long id){

        TimeTable targetTimeTable = timeTableRepository.findTimeTableById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_TIMETABLE_ID));

        return new TimeTableResponseServiceDto(targetTimeTable);
    }

    @Transactional(readOnly = true)
    public List<TimeTableResponseServiceDto> findAll(){

        return timeTableRepository.findAllBy()
                .orElseThrow(() -> new CustomException(ErrorCode.EMPTY_TIMETABLE))
                .stream()
                .map(TimeTableResponseServiceDto::new)
                .collect(Collectors.toList());

    }
    @Transactional(readOnly = true)
    public List<TimeTableResponseServiceDto> findAll(Specification<TimeTable> specification, Pageable pageable){

        return timeTableRepository.findAll(specification,pageable)
                .stream()
                .map(TimeTableResponseServiceDto::new)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<TimeTableResponseServiceDto> findAll(Specification<TimeTable> specification){

        return timeTableRepository.findAll(specification)
                .stream()
                .map(TimeTableResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long add(TimeTableAddRequestServiceDto timeTableAddRequestServiceDto,String who){
        TimeTable targetTimeTable = timeTableAddRequestServiceDto.toEntity();
        targetTimeTable.setCreateBy(who);
        targetTimeTable.setModifiedBy(who);
        return timeTableRepository.save(targetTimeTable).getId();
    }

    @Transactional
    public Long delete(Long id){
        TimeTable targetTimeTable =  timeTableRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_TIMETABLE_ID));

        timeTableRepository.delete(targetTimeTable);

        return id;
    }

    @Transactional
    public TimeTableResponseServiceDto update(Long id, TimeTableUpdateRequestServiceDto timeTableUpdateRequestServiceDto,String who){

        TimeTable targetTimeTable = timeTableRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_TIMETABLE_ID));

        targetTimeTable.update(timeTableUpdateRequestServiceDto.toEntity(),who);

        timeTableRepository.save(targetTimeTable);

        return new TimeTableResponseServiceDto(targetTimeTable);
    }
}
