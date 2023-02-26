package com.jaramgroupware.attendance.dto.event.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventUpdateRequestServiceDto;

import com.jaramgroupware.attendance.utlis.validation.dateTime.DateTimeCheck;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@DateTimeCheck(startDateTime = "startDateTime",endDateTime = "endDateTime")
public class EventUpdateRequestControllerDto {


    @NotEmpty(message = "name -> 해당 필드는 필수입니다.")
    @Size.List({
            @Size(min = 1, message = "name -> 해당 필드는 최소 1자리 이상이여야 합니다."),
            @Size(max = 50, message = "name -> 해당 필드는 50자 이하여야 합니다.")
    })
    private String name;

    @Size(max = 255,message = "index -> 해당 필드는 255자 이하여야 합니다.")
    private String index;

    @NotNull(message = "start_date_time -> 해당 필드는 필수입니다.")
    private LocalDateTime startDateTime;

    @NotNull(message = "end_date_time -> 해당 필드는 필수입니다.")
    private LocalDateTime endDateTime;

    public EventUpdateRequestServiceDto toServiceDto(String who){
        return EventUpdateRequestServiceDto.builder()
                .name(name)
                .index(index)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .modifiedBy(who)
                .build();
    }
}
