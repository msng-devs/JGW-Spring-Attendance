package com.jaramgroupware.attendance.web.controller;

import com.jaramgroupware.attendance.domain.event.EventSpecification;
import com.jaramgroupware.attendance.domain.event.EventSpecificationBuilder;
import com.jaramgroupware.attendance.domain.timeTable.TimeTableDateTimes;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventResponseControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.dto.others.controllerDto.MessageResponseDto;
import com.jaramgroupware.attendance.service.EventService;
import com.jaramgroupware.attendance.utlis.validation.pageable.PageableValid;
import com.jaramgroupware.attendance.web.aop.authorization.Auth;
import com.jaramgroupware.attendance.web.aop.authorization.RBAC;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/event")
@RestController
public class EventApiController {

    private final EventService eventService;
    private final EventSpecificationBuilder eventSpecificationBuilder;

    @Auth
    @GetMapping("/{eventId}")
    public EntityModel<EventResponseControllerDto> findEventById(@PathVariable Long eventId){
        var targetEventInfo = eventService.findEventById(eventId);
        var data = targetEventInfo.toControllerDto();

        return EntityModel.of(data,
                linkTo(methodOn(EventApiController.class).findEventById(eventId)).withSelfRel());
    }

    @RBAC(role = 4)
    @PostMapping
    public EntityModel<EventResponseControllerDto> createEvent(
            @RequestBody @Valid EventAddRequestControllerDto requestDto,
            @RequestHeader(value = "user_pk") String userUid){

        var newEventInfo = eventService.createEvent(requestDto.toServiceDto(userUid));
        var data = newEventInfo.toControllerDto();

        return EntityModel.of(data,
                linkTo(methodOn(EventApiController.class).findEventById(data.getId())).withSelfRel());
    }

    @RBAC(role = 4)
    @PutMapping("/{eventId}")
    public EntityModel<EventResponseControllerDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateRequestControllerDto requestDto,
            @RequestHeader(value = "user_pk") String userUid){

        var newEventInfo = eventService.updateEvent(requestDto.toServiceDto(userUid));
        var data = newEventInfo.toControllerDto();

        return EntityModel.of(data,
                linkTo(methodOn(EventApiController.class).findEventById(data.getId())).withSelfRel());
    }

    @RBAC(role = 4)
    @DeleteMapping("/{eventId}")
    public ResponseEntity<MessageResponseDto> updateEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(new MessageResponseDto("OK"));
    }

    @Auth
    @GetMapping
    public ResponseEntity<List<EventResponseControllerDto>> getEventAll(
            @PageableDefault(page = 0,size = 20,sort = "id",direction = Sort.Direction.DESC)
            @PageableValid(sortKeys = {"id","name","index","startDateTime","endDateTime","createdDateTime","modifiedDateTime","createBy","modifiedBy"},maxPageSize = 200) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> queryParam){

        //Specification 등록
        EventSpecification spec = eventSpecificationBuilder.toSpec(queryParam);

        var results = eventService.findAll(spec,pageable)
                    .stream()
                    .map(EventResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }
}
