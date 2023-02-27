package com.jaramgroupware.attendance.dto.attendance.serviceDto;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceAddRequestServiceDto {

    private Integer attendanceTypeId;
    private String memberId;
    private Long timeTableId;
    private String index;
    private String createdBy;

    public Attendance toEntity(Member member,AttendanceType attendanceType,TimeTable timeTable){

        Attendance newAttendance = Attendance.builder()
                .attendanceType(attendanceType)
                .member(member)
                .timeTable(timeTable)
                .index(index)
                .build();

        newAttendance.setModifiedBy(createdBy);
        newAttendance.setCreateBy(createdBy);

        return newAttendance;
    }

}
