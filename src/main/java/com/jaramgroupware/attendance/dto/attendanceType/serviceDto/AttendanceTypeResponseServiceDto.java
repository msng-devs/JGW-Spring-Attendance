package com.jaramgroupware.attendance.dto.attendanceType.serviceDto;

import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.dto.attendanceCode.serviceDto.AttendanceCodeResponseServiceDto;
import com.jaramgroupware.attendance.dto.attendanceType.controllerDto.AttendanceTypeResponseControllerDto;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class AttendanceTypeResponseServiceDto {

    private Integer id;
    private String name;

    public AttendanceTypeResponseServiceDto(AttendanceType attendanceType){
        id = attendanceType.getId();
        name = attendanceType.getName();
    }

    public AttendanceTypeResponseControllerDto toControllerDto() {
        return AttendanceTypeResponseControllerDto.builder()
                .id(id)
                .name(name)
                .build();
    }
}
