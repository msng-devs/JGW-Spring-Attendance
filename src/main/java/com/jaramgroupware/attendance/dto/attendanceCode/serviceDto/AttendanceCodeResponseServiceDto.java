package com.jaramgroupware.attendance.dto.attendanceCode.serviceDto;

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
}
