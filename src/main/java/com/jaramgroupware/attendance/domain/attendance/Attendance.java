package com.jaramgroupware.attendance.domain.attendance;


import com.jaramgroupware.attendance.domain.BaseEntity;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import jakarta.persistence.*;
import lombok.*;


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
@Entity(name = "ATTENDANCE")
public class Attendance extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDANCE_PK")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_MEMBER_PK",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIMETABLE_TIMETABLE_PK",nullable = false)
    private TimeTable timeTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTENDANCE_TYPE_ATTENDANCE_TYPE_PK",nullable = false)
    private AttendanceType attendanceType;

    @Column(name = "ATTENDANCE_INDEX")
    private String index;

    public void update(Attendance attendance){
        if(attendance.getAttendanceType() != null) attendanceType = attendance.getAttendanceType();
        index = attendance.getIndex();
        modifiedBy = attendance.getModifiedBy();
    }

}
