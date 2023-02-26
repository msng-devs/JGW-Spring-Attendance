package com.jaramgroupware.attendance.dto.attendanceCode.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AttendanceCodeAddRequestControllerDto {
    @NotNull(message = "exp_sec -> 해당 필드는 필수입니다.")
    @Max(value = 2592000,message = "exp_sec -> 설정가능한 최대 유효 시간은 2592000(초) 입니다.")
    @Min(value = -1,message = "exp_sec -> 해당 필드에 음수는 사용할 수 없습니다. 만약 영구적인 출결 코드를 발급할려면 (-1)을 입력하세요. ")
    private Long expSec;
}
