package com.jaramgroupware.attendance.service;

import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.event.EventRepository;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventAddRequestServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventUpdateRequestServiceDto;
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
public class EventService {

    @Autowired
    private final EventRepository eventRepository;

    @Transactional
    public Long add(EventAddRequestServiceDto eventAddRequestServiceDto,String who){
        Event targetEvent = eventAddRequestServiceDto.toEntity();
        targetEvent.setCreateBy(who);
        targetEvent.setModifiedBy(who);
        return eventRepository.save(targetEvent).getId();
    }

    @Transactional
    public Long delete(Long id){

        Event targetEvent =  eventRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_EVENT_ID));

        eventRepository.delete(targetEvent);

        return id;
    }

    @Transactional(readOnly = true)
    public EventResponseServiceDto findById(Long id){

        Event targetEvent = eventRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_EVENT_ID));

        return new EventResponseServiceDto(targetEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponseServiceDto> findAll(){

        return eventRepository.findAllBy()
                .orElseThrow(()-> new CustomException(ErrorCode.EMPTY_EVENT))
                .stream().map(EventResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventResponseServiceDto> findAll(Specification<Event> specification, Pageable pageable){

        return eventRepository.findAll(specification,pageable)
                .stream().map(EventResponseServiceDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponseServiceDto update(Long id, EventUpdateRequestServiceDto updateRequestServiceDto,String who){

        Event targetEvent = eventRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_EVENT_ID));

        targetEvent.update(updateRequestServiceDto.toEntity(),who);

        eventRepository.save(targetEvent);

        return new EventResponseServiceDto(targetEvent);
    }
}
