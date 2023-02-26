package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.event.EventRepository;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableDateTimes;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableRepository;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceErrorCode;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final TimeTableRepository timeTableRepository;
    @Transactional(readOnly = true)
    public EventResponseServiceDto findEventById(Long id){
        var targetEvent = eventRepository.findById(id).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_EVENT));

        return new EventResponseServiceDto(targetEvent);
    }

    @Transactional
    public EventResponseServiceDto createEvent(EventAddRequestServiceDto requestDto){
        var newEvent = requestDto.toEntity();

        return new EventResponseServiceDto(eventRepository.save(newEvent));
    }

    @Transactional
    public void deleteEvent(Long id){
        eventRepository.deleteByIdId(id);
    }

    @Transactional(readOnly = true)
    public TimeTableDateTimes test(){
        return timeTableRepository.findTimeTableDateTimesByEventId(1L);
    }

    @Transactional
    public EventResponseServiceDto updateEvent(EventUpdateRequestServiceDto requestDto){
        var targetEvent = eventRepository.findById(requestDto.getId()).orElseThrow(() -> new ServiceException(ServiceErrorCode.INVALID_EVENT));
        targetEvent.update(requestDto.toEntity());

        return new EventResponseServiceDto(targetEvent);
    }
}
