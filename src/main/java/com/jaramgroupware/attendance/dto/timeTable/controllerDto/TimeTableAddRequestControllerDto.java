package com.jaramgroupware.attendance.dto.timeTable.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.dto.timeTable.serviceDto.TimeTableAddRequestServiceDto;
import com.jaramgroupware.attendance.utlis.validation.dateTime.DateTimeCheck;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;


@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@DateTimeCheck(startDateTime = "startDateTime",endDateTime = "endDateTime")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TimeTableAddRequestControllerDto {

    @NotEmpty(message = "name -> 해당 필드는 필수 입니다.")
    @Size.List({
            @Size(min = 1, message = "name -> 해당 필드는 최소 1자리 이상이여야 합니다."),
            @Size(max = 50, message = "name -> 해당 필드는 50자 이하여야 합니다.")
    })
    private String name;

    @Size(max = 200, message = "index -> 해당 필드는 200자 이하여야 합니다.")
    private String index;

    @NotNull(message = "event_id -> 해당 필드는 필수입니다.")
    @Positive(message = "event_id -> 해당 필드는 양수만 입력 가능합니다.")
    private Long eventID;

    @NotNull(message = "start_date_time -> 해당 필드는 필수 입니다.")
    private LocalDateTime startDateTime;

    @NotNull(message = "end_date_time -> 해당 필드는 필수 입니다.")
    private LocalDateTime endDateTime;

    public TimeTableAddRequestServiceDto toServiceDto(String who){

        return TimeTableAddRequestServiceDto.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .index(index)
                .name(name)
                .createdBy(who)
                .eventID(eventID)
                .build();
    }
}
