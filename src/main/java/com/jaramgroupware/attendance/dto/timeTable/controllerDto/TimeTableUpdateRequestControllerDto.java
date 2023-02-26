package com.jaramgroupware.attendance.dto.timeTable.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableUpdateRequestServiceDto;
import com.jaramgroupware.attendance.utlis.validation.dateTime.DateTimeCheck;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DateTimeCheck(startDateTime = "startDateTime",endDateTime = "endDateTime")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TimeTableUpdateRequestControllerDto {

    @NotEmpty(message = "name -> 해당 필드는 필수 입니다.")
    @Size.List({
            @Size(min = 1, message = "name -> 해당 필드는 최소 1자리 이상이여야 합니다."),
            @Size(max = 50, message = "name -> 해당 필드는 50자 이하여야 합니다.")
    })
    private String name;

    @Size(max = 50, message = "index -> 해당 필드는 200자 이하여야 합니다.")
    private String index;

    @NotNull(message = "start_date_time -> 해당 필드는 필수 입니다.")
    private LocalDateTime startDateTime;

    @NotNull(message = "end_date_time -> 해당 필드는 필수 입니다.")
    private LocalDateTime endDateTime;

    public TimeTableUpdateRequestServiceDto toServiceDto(Long id,String who){
        return TimeTableUpdateRequestServiceDto.builder()
                .endDateTime(endDateTime)
                .id(id)
                .index(index)
                .name(name)
                .startDateTime(startDateTime)
                .modifiedBy(who)
                .build();
    }
}
