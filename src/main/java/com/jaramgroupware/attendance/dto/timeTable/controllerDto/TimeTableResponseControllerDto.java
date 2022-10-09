package com.jaramgroupware.attendance.dto.timeTable.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TimeTableResponseControllerDto {

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

}
