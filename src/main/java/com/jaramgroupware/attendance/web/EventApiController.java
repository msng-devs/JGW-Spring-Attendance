package com.jaramgroupware.attendance.web;


import com.jaramgroupware.attendance.domain.event.EventSpecification;
import com.jaramgroupware.attendance.domain.event.EventSpecificationBuilder;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventAddRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventIdResponseControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventResponseControllerDto;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventUpdateRequestControllerDto;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventResponseServiceDto;
import com.jaramgroupware.attendance.service.EventService;
import com.jaramgroupware.attendance.utils.validation.PageableValid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/event")
public class EventApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventService eventService;
    private final EventSpecificationBuilder eventSpecificationBuilder;

    @PostMapping
    public ResponseEntity<EventIdResponseControllerDto> addEvent(
            @RequestBody @Valid EventAddRequestControllerDto eventAddRequestControllerDto,
            @RequestHeader("user_uid") String uid){

        Long id = eventService.add(eventAddRequestControllerDto.toServiceDto(),uid);

        return ResponseEntity.ok(new EventIdResponseControllerDto(id));
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventResponseControllerDto> getEventById(
            @PathVariable Long eventId,
            @RequestHeader("user_uid") String uid){

        EventResponseControllerDto result = eventService.findById(eventId).toControllerDto();

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseControllerDto>> getEventAll(
            @PageableDefault(page = 0,size = 1000,sort = "id",direction = Sort.Direction.DESC)
            @PageableValid(sortKeys =
                    {"id","name","index","startDateTime","endDateTime","createdDateTime","modifiedDateTime","createBy","modifiedBy"}
            ) Pageable pageable,
            @RequestParam(required = false) MultiValueMap<String, String> queryParam,
            @RequestHeader("user_uid") String uid){

        //limit 확인 및 추가
        int limit = queryParam.containsKey("limit") ? Integer.parseInt(Objects.requireNonNull(queryParam.getFirst("limit"))) : -1;

        //Specification 등록
        EventSpecification spec = eventSpecificationBuilder.toSpec(queryParam);

        List<EventResponseControllerDto> results;

        //limit true
        if(limit > 0){
            results = eventService.findAll(spec, PageRequest.of(0, limit, pageable.getSort()))
                    .stream()
                    .map(EventResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());
        }

        else{
            results = eventService.findAll(spec,pageable)
                    .stream()
                    .map(EventResponseServiceDto::toControllerDto)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(results);
    }

    @DeleteMapping("{eventId}")
    public ResponseEntity<EventIdResponseControllerDto> delEvent(
            @PathVariable Long eventId,
            @RequestHeader("user_uid") String uid){

        eventService.delete(eventId);

        return ResponseEntity.ok(new EventIdResponseControllerDto(eventId));
    }

    @PutMapping("{eventId}")
    public ResponseEntity<EventResponseControllerDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateRequestControllerDto eventUpdateRequestControllerDto,
            @RequestHeader("user_uid") String uid){

        EventResponseControllerDto result = eventService.update(eventId,eventUpdateRequestControllerDto.toServiceDto(),uid).toControllerDto();

        return ResponseEntity.ok(result);
    }
}
