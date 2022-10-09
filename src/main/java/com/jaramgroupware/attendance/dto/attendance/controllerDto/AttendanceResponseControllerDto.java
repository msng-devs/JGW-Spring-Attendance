package com.jaramgroupware.attendance.dto.attendance.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceResponseControllerDto {

    private Integer attendanceTypeID;
    private String attendanceTypeName;
    private Long timeTableID;
    private String timeTableName;
    private String memberID;
    private String memberName;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private String createBy;
    private String modifyBy;
    private String index;

}
