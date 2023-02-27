package com.jaramgroupware.attendance.dto.attendance.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.attendanceType.AttendanceType;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceUpdateRequestServiceDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;



@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceUpdateRequestControllerDto {

    @Positive(message = "attendance_type_id -> 해당 필드는 양수만 입력 가능합니다.")
    @NotNull(message = "attendance_type_id -> 해당 필드는 필수입니다.")
    private Integer attendanceTypeID;

    @Size(max = 255 ,message= "index -> 해당 필드는 255자 이하여야합니다.")
    private String index;

    public AttendanceUpdateRequestServiceDto toServiceDto(String who,Long attendanceId){
        return AttendanceUpdateRequestServiceDto.builder()
                .attendanceId(attendanceId)
                .index(index)
                .attendanceTypeId(attendanceTypeID)
                .modifiedBy(who)
                .build();
    }
}
