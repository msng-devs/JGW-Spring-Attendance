package com.jaramgroupware.attendance.dto.timeTable.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TimeTableResponseControllerDto {

    private Long id;
    private String name;
    private LocalDateTime modifiedDateTime;
    private String modifyBy;
    private Long eventID;
    private String index;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

}
