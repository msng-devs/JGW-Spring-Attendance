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
public class AttendanceAddServiceDto {

    private AttendanceType attendanceType;
    private Member member;
    private TimeTable timeTable;
    private String index;
    private String who;

    public Attendance toEntity(){
        return Attendance.builder()
                .attendanceType(attendanceType)
                .member(member)
                .timeTable(timeTable)
                .index(index)
                .build();
    }

    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }

}
