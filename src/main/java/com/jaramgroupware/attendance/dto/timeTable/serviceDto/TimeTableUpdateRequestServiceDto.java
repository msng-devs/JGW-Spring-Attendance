package com.jaramgroupware.attendance.dto.timeTable.serviceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TimeTableUpdateRequestServiceDto {

    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public TimeTable toEntity(){
        return TimeTable.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .name(name)
                .build();
    }
    @Override
    public boolean equals(Object o){
        return this.toString().equals(o.toString());
    }
}
