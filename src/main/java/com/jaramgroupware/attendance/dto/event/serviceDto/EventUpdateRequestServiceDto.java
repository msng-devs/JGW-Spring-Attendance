package com.jaramgroupware.attendance.dto.event.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.event.Event;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateRequestServiceDto {

    private Long id;
    private String name;
    private String index;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String modifiedBy;

    public Event toEntity(){
        var event = Event.builder()
                .name(name)
                .index(index)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        event.setModifiedBy(modifiedBy);

        return event;
    }
}
