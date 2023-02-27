package com.jaramgroupware.attendance.dto.attendance.serviceDto;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import lombok.*;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUpdateRequestServiceDto {

    private Long attendanceId;
    private Integer attendanceTypeId;
    private String index;
    private String modifiedBy;

    public Attendance toEntity(AttendanceType attendanceType){
        var newAttendance = Attendance.builder()
                .attendanceType(attendanceType)
                .index(index)
                .build();
        newAttendance.setModifiedBy(modifiedBy);

        return newAttendance;
    }

    public Attendance toEntity(){
        var newAttendance = Attendance.builder()
                .attendanceType(null)
                .index(index)
                .build();
        newAttendance.setModifiedBy(modifiedBy);

        return newAttendance;
    }
}
