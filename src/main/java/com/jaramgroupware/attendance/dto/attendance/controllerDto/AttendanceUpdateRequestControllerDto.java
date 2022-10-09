package com.jaramgroupware.attendance.dto.attendance.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceUpdateRequestServiceDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceUpdateRequestControllerDto {

    @Positive(message = "attendance_type_id의 형식이 잘못되었습니다!")
    @NotNull(message = "attendance_type_id가 비워져 있습니다!")
    private Integer attendanceTypeID;

    @Size(max = 255 ,message= "index에 입력 가능한 전체 글자수는 255자입니다. ")
    private String index;

    public AttendanceUpdateRequestServiceDto toServiceDto(AttendanceType attendanceType){
        return AttendanceUpdateRequestServiceDto.builder()
                .index(index)
                .attendanceType(attendanceType)
                .build();
    }
}
