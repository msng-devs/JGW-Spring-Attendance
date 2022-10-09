package com.jaramgroupware.attendance.dto.attendanceType.serviceDto;

import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.dto.attendanceType.controllerDto.AttendanceTypeResponseControllerDto;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceTypeResponseServiceDto {

    private Integer id;
    private String name;

    public AttendanceTypeResponseServiceDto(AttendanceType attendanceType){
        id = attendanceType.getId();
        name = attendanceType.getName();
    }

    public AttendanceType toEntity(){
        return AttendanceType.builder()
                .id(id)
                .name(name)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }

    public AttendanceTypeResponseControllerDto toControllerDto() {
        return AttendanceTypeResponseControllerDto.builder()
                .id(id)
                .name(name)
                .build();
    }
}
