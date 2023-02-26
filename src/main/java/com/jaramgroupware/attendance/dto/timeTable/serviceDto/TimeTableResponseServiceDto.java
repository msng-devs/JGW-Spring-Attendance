package com.jaramgroupware.attendance.dto.timeTable.serviceDto;

import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.timeTable.controllerDto.TimeTableResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
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
    private String index;

    public TimeTableResponseServiceDto(TimeTable timeTable){
        id = timeTable.getId();
        name = timeTable.getName();
        createdDateTime = timeTable.getCreatedDateTime();
        modifiedDateTime = timeTable.getModifiedDateTime();
        createdBy = timeTable.getCreateBy();
        modifyBy = timeTable.getModifiedBy();
        eventID = timeTable.getEvent().getId();
        eventName = timeTable.getEvent().getName();
        startDateTime = timeTable.getStartDateTime();
        endDateTime = timeTable.getEndDateTime();
        index = timeTable.getIndex();

    }

    public TimeTableResponseControllerDto toControllerDto(){
        return TimeTableResponseControllerDto.builder()
                .endDateTime(endDateTime)
                .startDateTime(startDateTime)
                .createdBy(createdBy)
                .createdDateTime(createdDateTime)
                .eventID(eventID)
                .id(id)
                .index(index)
                .name(name)
                .build();
    }
}
