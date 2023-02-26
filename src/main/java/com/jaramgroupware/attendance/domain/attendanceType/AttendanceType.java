package com.jaramgroupware.attendance.domain.attendanceType;

import jakarta.persistence.*;
import lombok.*;



@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ATTENDANCE_TYPE")
public class AttendanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDANCE_TYPE_PK")
    private Integer id;

    @Column(name = "ATTENDANCE_TYPE_NAME",nullable = false,unique = true,length = 45)
    private String name;


}
