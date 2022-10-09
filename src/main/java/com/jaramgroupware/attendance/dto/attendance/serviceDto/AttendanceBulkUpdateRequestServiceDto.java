package com.jaramgroupware.attendance.dto.attendance.serviceDto;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceBulkUpdateRequestServiceDto {

    private TimeTable timeTable;
    private Member member;
    private AttendanceType attendanceType;
    private String index;

    public Attendance toEntity(){
        return Attendance.builder()
                .timeTable(timeTable)
                .member(member)
                .attendanceType(attendanceType)
                .index(index)
                .build();
    }

    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
