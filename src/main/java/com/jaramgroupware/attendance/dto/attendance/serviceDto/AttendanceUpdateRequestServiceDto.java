package com.jaramgroupware.attendance.dto.attendance.serviceDto;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUpdateRequestServiceDto {

    private AttendanceType attendanceType;
    private String index;

    public Attendance toEntity(){
        return Attendance.builder()
                .attendanceType(attendanceType)
                .index(index)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
