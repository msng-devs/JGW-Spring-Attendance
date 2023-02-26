package com.jaramgroupware.attendance.dto.event.controllerDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventResponseControllerDto {

    private Long id;
    private String name;
    private String index;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

}
