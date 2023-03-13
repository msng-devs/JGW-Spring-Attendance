package com.jaramgroupware.attendance.dto.attendanceCode.serviceDto;

import com.jaramgroupware.attendance.dto.attendanceCode.controllerDto.AttendanceCodeResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AttendanceCodeResponseServiceDto {
    private String code;
    private LocalDateTime expAt;

    public AttendanceCodeResponseControllerDto toControllerDto(){
        return AttendanceCodeResponseControllerDto.builder()
                .code(code)
                .expAt(expAt)
                .build();
    }
}
