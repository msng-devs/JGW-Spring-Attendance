package com.jaramgroupware.attendance.domain.attendance;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.BaseEntity;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverrides({
        @AttributeOverride(name = "createdDateTime",column = @Column(name = "ATTENDANCE_CREATED_DTTM")),
        @AttributeOverride(name = "modifiedDateTime",column = @Column(name = "ATTENDANCE_MODIFIED_DTTM")),
        @AttributeOverride(name = "createBy",column = @Column(name = "ATTENDANCE_CREATED_BY",length = 30)),
        @AttributeOverride(name = "modifiedBy",column = @Column(name = "ATTENDANCE_MODIFIED_BY",length = 30)),
})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Entity(name = "ATTENDANCE")
@IdClass(AttendanceID.class)
public class Attendance extends BaseEntity implements Serializable{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_MEMBER_PK",nullable = false)
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIMETABLE_TIMETABLE_PK",nullable = false)
    private TimeTable timeTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTENDANCE_TYPE_ATTENDANCE_TYPE_PK",nullable = false)
    private AttendanceType attendanceType;

    @Column(name = "ATTENDANCE_INDEX")
    private String index;

    public void update(Attendance attendance,String modified){
        attendanceType = attendance.getAttendanceType();
        index = attendance.getIndex();
        modifiedBy = modified;
    }

    public AttendanceID getId(){
        return AttendanceID.builder()
                .member(member)
                .timeTable(timeTable)
                .build();
    }

}
