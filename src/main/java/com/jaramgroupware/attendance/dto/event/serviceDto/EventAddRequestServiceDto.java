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
public class EventAddRequestServiceDto {

    private String name;
    private String index;
    private LocalDateTime starDateTime;
    private LocalDateTime endDateTime;
    private String createBy;

    public Event toEntity(){
        var event = Event.builder()
                .name(name)
                .index(index)
                .startDateTime(starDateTime)
                .endDateTime(endDateTime)
                .build();
        event.setCreateBy(createBy);
        event.setModifiedBy(createBy);

        return event;
    }

}
