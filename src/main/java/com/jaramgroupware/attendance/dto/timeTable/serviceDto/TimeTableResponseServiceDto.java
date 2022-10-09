package com.jaramgroupware.attendance.dto.timeTable.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TimeTableResponseServiceDto {

    private Long id;
    private String name;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private String createdBy;
    private String modifyBy;
    private Long eventID;
    private String eventName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public TimeTable toEntity(){
        TimeTable res =  TimeTable.builder()
                .id(id)
                .name(name)
                .event(
                        Event.builder()
                                .id(eventID)
                                .name(eventName)
                                .build()
                )
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        res.setCreatedDateTime(createdDateTime);
        res.setCreateBy(createdBy);
        res.setModifiedBy(modifyBy);
        res.setModifiedDateTime(modifiedDateTime);

        return res;
    }
    public TimeTableResponseServiceDto(TimeTable timeTable){
        id = timeTable.getId();
        name = timeTable.getName();
        eventID = timeTable.getEvent().getId();
        eventName = timeTable.getEvent().getName();
        startDateTime = timeTable.getStartDateTime();
        endDateTime = timeTable.getEndDateTime();
        createdDateTime = timeTable.getCreatedDateTime();
        modifiedDateTime = timeTable.getModifiedDateTime();
        createdBy = timeTable.getCreateBy();
        modifyBy = timeTable.getModifiedBy();
    }

    public TimeTableResponseControllerDto toControllerDto(){
        return TimeTableResponseControllerDto
                .builder()
                .id(id)
                .name(name)
                .eventID(eventID)
                .eventName(eventName)
                .createdBy(createdBy)
                .createdDateTime(createdDateTime)
                .modifyBy(modifyBy)
                .modifiedDateTime(modifiedDateTime)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }

}
