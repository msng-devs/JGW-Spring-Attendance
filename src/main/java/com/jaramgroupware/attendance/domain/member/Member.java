package com.jaramgroupware.attendance.domain.member;


import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.domain.role.Role;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "MEMBER")
public class Member{

    @Id
    @Column(name = "MEMBER_PK",length = 28)
    private String id;

    @Email
    @Column(name = "MEMBER_EMAIL",nullable = false,length = 255)
    private String email;

    @Column(name = "MEMBER_NM",nullable = false,length = 45)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ROLE_PK",nullable = false)
    private Role role;

    @Column(name = "MEMBER_STATUS",nullable = false)
    private boolean isStatus;

    /**
     * 해당 member의 정보를 바탕으로 새로운 attendance를 생성합니다/
     * @param timeTable
     * @param attendanceType
     * @return
     */
    public Attendance createSystemAttendance(TimeTable timeTable, AttendanceType attendanceType){
        var newAttendance = Attendance
                .builder()
                .attendanceType(attendanceType)
                .timeTable(timeTable)
                .member(this)
                .index("시스템에 의해 자동으로 생성되었습니다.")
                .build();
        newAttendance.setCreateBy("system");
        newAttendance.setModifiedBy("system");

        return newAttendance;
    }
}
