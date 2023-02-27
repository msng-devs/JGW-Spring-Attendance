package com.jaramgroupware.attendance.dto.attendance.serviceDto;

import com.jaramgroupware.attendance.domain.attendance.Attendance;
import com.jaramgroupware.attendance.dto.attendance.controllerDto.AttendanceResponseControllerDto;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponseServiceDto {

    private Integer attendanceTypeID;
    private String memberID;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private String createBy;
    private String modifyBy;
    private Long timeTableID;
    private String timeTableName;
    private String index;

    public AttendanceResponseControllerDto toControllerDto(){
        return AttendanceResponseControllerDto.builder()
                .attendanceTypeID(attendanceTypeID)
                .memberID(memberID)
                .createdDateTime(createdDateTime)
                .modifiedDateTime(modifiedDateTime)
                .timeTableID(timeTableID)
                .createBy(createBy)
                .modifyBy(modifyBy)
                .index(index)
                .build();
    }

    public AttendanceResponseServiceDto(Attendance attendance){
        attendanceTypeID = attendance.getAttendanceType().getId();
        memberID = attendance.getMember().getId();
        timeTableID = attendance.getTimeTable().getId();
        index = attendance.getIndex();
        createdDateTime = attendance.getCreatedDateTime();
        modifiedDateTime = attendance.getModifiedDateTime();
        createBy = attendance.getCreateBy();
        modifyBy = attendance.getModifiedBy();
    }
}
