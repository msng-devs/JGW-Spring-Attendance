package com.jaramgroupware.attendance.dto.event.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.dto.event.serviceDto.EventAddRequestServiceDto;
import com.jaramgroupware.attendance.utils.validation.DateTimeCheck;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@DateTimeCheck(startDateTime = "startDateTime",endDateTime = "endDateTime")
public class EventAddRequestControllerDto {


    @NotEmpty(message = "Event 이름이 비워져있습니다!")
    @Size(max = 50,min=1,message = "Event 이름은 1자 이상 50자 이하이여야 합니다.")
    private String name;

    @Size(max = 255,message = "입력가능한 최대 글자수는 255자입니다.")
    private String index;


    @NotNull(message = "startTime 은 반드시 입력해야 합니다!")
//    @FutureOrPresent(message = "startTime에 과거 시간은 사용할 수 없습니다!")
    private LocalDateTime startDateTime;


    @NotNull(message = "endTime은 반드시 입력해야 합니다!")
//    @FutureOrPresent(message = "endTime에 과거 시간은 사용할 수 없습니다!")
    private LocalDateTime endDateTime;


    public EventAddRequestServiceDto toServiceDto(){
        return EventAddRequestServiceDto.builder()
                .name(name)
                .index(index)
                .starDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
    }
}
