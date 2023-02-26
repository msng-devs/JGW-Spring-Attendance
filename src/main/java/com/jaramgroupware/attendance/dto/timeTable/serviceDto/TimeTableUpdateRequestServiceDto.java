package com.jaramgroupware.attendance.dto.timeTable.serviceDto;

import com.jaramgroupware.attendance.domain.event.Event;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTableUpdateRequestServiceDto {

    private Long id;
    private String name;
    private String index;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String modifiedBy;

    public TimeTable toEntity(){

        var timetable = TimeTable.builder()
                .endDateTime(endDateTime)
                .startDateTime(startDateTime)
                .name(name)
                .index(index)
                .build();

        timetable.setModifiedBy(modifiedBy);

        return timetable;
    }

}
