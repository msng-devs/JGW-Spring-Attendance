package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.domain.timeTable.TimeTableDateTimes;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventResponseControllerDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/event")
@RestController
public class EventApiController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EntityModel<EventResponseControllerDto> findEventById(@PathVariable String eventId){

    }

}
