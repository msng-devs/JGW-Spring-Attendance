package com.jaramgroupware.attendance.dto.attendanceCode.serviceDto;

import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AttendanceCodeAddRequestServiceDto {
    private String code;
    private String timeTableId;
    private Long expSec;
}
