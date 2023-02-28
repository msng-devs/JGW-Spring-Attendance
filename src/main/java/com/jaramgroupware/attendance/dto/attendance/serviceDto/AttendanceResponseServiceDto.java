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

    private Long id;
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
                .id(id)
                .attendanceTypeID(attendanceTypeID)
                .memberID(memberID)
                .modifiedDateTime(modifiedDateTime)
                .timeTableID(timeTableID)
                .modifyBy(modifyBy)
                .index(index)
                .build();
    }

    public AttendanceResponseServiceDto(Attendance attendance){
        id = attendance.getId();
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
