package com.jaramgroupware.attendance.dto.attendance.serviceDto;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponseServiceDto {

    private Integer attendanceTypeID;
    private String attendanceTypeName;
    private String memberID;
    private String memberName;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private String createBy;
    private String modifyBy;
    private Long timeTableID;
    private String timeTableName;
    private String index;

    public AttendanceResponseControllerDto toControllerDto(){
        return AttendanceResponseControllerDto.builder()
                .attendanceTypeName(attendanceTypeName)
                .attendanceTypeID(attendanceTypeID)
                .memberID(memberID)
                .memberName(memberName)
                .createdDateTime(createdDateTime)
                .modifiedDateTime(modifiedDateTime)
                .timeTableID(timeTableID)
                .timeTableName(timeTableName)
                .createBy(createBy)
                .modifyBy(modifyBy)
                .index(index)
                .build();
    }

    public AttendanceResponseServiceDto(Attendance attendance){
        attendanceTypeID = attendance.getAttendanceType().getId();
        attendanceTypeName = attendance.getAttendanceType().getName();
        memberID = attendance.getMember().getId();
        memberName = attendance.getMember().getName();
        timeTableID = attendance.getTimeTable().getId();
        timeTableName = attendance.getTimeTable().getName();
        index = attendance.getIndex();
        createdDateTime = attendance.getCreatedDateTime();
        modifiedDateTime = attendance.getModifiedDateTime();
        createBy = attendance.getCreateBy();
        modifyBy = attendance.getModifiedBy();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
