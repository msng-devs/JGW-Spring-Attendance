package com.jaramgroupware.attendance.dto.timeTable.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.utlis.validation.dateTime.DateTimeCheck;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTableAddRequestServiceDto {

    private String name;
    private String index;
    private Long eventID;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String createdBy;

    public TimeTable toEntity(Event event){
        var timetable = TimeTable.builder()
                .endDateTime(endDateTime)
                .startDateTime(startDateTime)
                .event(event)
                .name(name)
                .index(index)
                .build();
        timetable.setCreateBy(createdBy);
        timetable.setModifiedBy(createdBy);

        return timetable;
    }

}
