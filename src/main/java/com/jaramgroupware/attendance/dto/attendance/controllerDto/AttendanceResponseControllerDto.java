package com.jaramgroupware.attendance.dto.attendance.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceResponseControllerDto {

    private Integer attendanceTypeID;
    private Long timeTableID;
    private String memberID;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private String createBy;
    private String modifyBy;
    private String index;

}
