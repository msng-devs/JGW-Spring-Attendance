package com.jaramgroupware.attendance.dto.event.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.dto.event.controllerDto.EventResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseServiceDto {

    private Long id;
    private String name;
    private String index;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public EventResponseControllerDto toControllerDto(){

        return EventResponseControllerDto.builder()
                .id(id)
                .name(name)
                .index(index)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

    public EventResponseServiceDto(Event event){
        id = event.getId();
        name = event.getName();
        index = event.getIndex();
        startDateTime = event.getStartDateTime();
        endDateTime = event.getEndDateTime();
    }

    public Event toEntity(){
        return Event.builder()
                .id(id)
                .name(name)
                .index(index)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }
}
