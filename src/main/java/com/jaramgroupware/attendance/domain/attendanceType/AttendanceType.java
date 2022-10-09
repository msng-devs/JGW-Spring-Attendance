package com.jaramgroupware.attendance.domain.attendanceType;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ATTENDANCE_TYPE")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDANCE_TYPE_PK")
    private Integer id;

    @Column(name = "ATTENDANCE_TYPE_NAME",nullable = false,unique = true,length = 45)
    private String name;


}
