package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.domain.timeTable.TimeTableDateTimes;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventResponseControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.service.EventService;
import com.jaramgroupware.attendance.web.aop.authorization.RBAC;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/event")
@RestController
public class EventApiController {

    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EntityModel<EventResponseControllerDto> findEventById(@PathVariable Long eventId){
        var targetEventInfo = eventService.findEventById(eventId);
        var data = targetEventInfo.toControllerDto();
    }

    @RBAC(role = 4)
    @PostMapping("/")
    public EntityModel<EventResponseControllerDto> createEvent(
            @RequestBody @Valid EventAddRequestControllerDto requestDto,
            @RequestHeader(value = "user_pk") String userUid){

        var newEventInfo = eventService.createEvent(requestDto.toServiceDto(userUid));
        var data = newEventInfo.toControllerDto();
    }

    @RBAC(role = 4)
    @PutMapping("/{eventId}")
    public EntityModel<EventResponseControllerDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateRequestControllerDto requestDto,
            @RequestHeader(value = "user_pk") String userUid){

        var newEventInfo = eventService.updateEvent(requestDto.toServiceDto(userUid));
        var data = newEventInfo.toControllerDto();
    }

    @RBAC(role = 4)
    @DeleteMapping("/{eventId}")
    public EntityModel<EventResponseControllerDto> updateEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
