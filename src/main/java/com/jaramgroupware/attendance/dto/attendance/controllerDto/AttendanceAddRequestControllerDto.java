package com.jaramgroupware.attendance.dto.attendance.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.dto.attendance.serviceDto.AttendanceAddRequestServiceDto;
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
public class AttendanceAddRequestControllerDto {

    @Positive(message = "time_table_id -> 해당 필드는 양수만 입력 가능합니다.")
    @NotNull(message = "time_table_id -> 해당 필드는 필수입니다.")
    private Long timeTableID;

    @NotEmpty(message = "member_id -> 해당 필드는 필수입니다.")
    @Size.List({
            @Size(min = 28, message = "member_id -> 해당 필드는 28자이여야합니다."),
            @Size(max = 28, message = "member_id -> 해당 필드는 28자이여야합니다.")
    })
    private String memberId;

    @Positive(message = "attendance_type_id -> 해당 필드는 양수만 입력 가능합니다.")
    @NotNull(message = "attendance_type_id -> 해당 필드는 필수입니다.")
    private Integer attendanceTypeID;

    @Size(max = 255 ,message= "index -> 해당 필드는 255자 이하여야합니다.")
    private String index;

    public AttendanceAddRequestServiceDto toServiceDto(String who){
        return AttendanceAddRequestServiceDto.builder()
                .index(index)
                .attendanceTypeId(attendanceTypeID)
                .createdBy(who)
                .timeTableId(timeTableID)
                .memberId(memberId)
                .build();

    }

}
