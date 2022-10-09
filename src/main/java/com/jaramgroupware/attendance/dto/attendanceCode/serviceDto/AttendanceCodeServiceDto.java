package com.jaramgroupware.attendance.dto.attendanceCode.serviceDto;

import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeResponseControllerDto;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AttendanceCodeServiceDto {

    private String code;
    private Long timeTableId;
    private Integer minute;

    public AttendanceCodeResponseControllerDto toControllerDto(){
        return AttendanceCodeResponseControllerDto.builder()
                .code(code)
                .timeTableId(timeTableId)
                .minute(minute)
                .build();
    }
}
