package com.jaramgroupware.attendance.domain.attendance;

import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class AttendanceID implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_MEMBER_PK",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIMETABLE_TIMETABLE_PK",nullable = false)
    private TimeTable timeTable;
}
