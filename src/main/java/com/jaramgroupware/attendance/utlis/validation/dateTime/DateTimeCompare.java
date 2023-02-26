package com.jaramgroupware.attendance.utlis.validation.dateTime;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeCompare {

    public boolean compare(LocalDateTime pastDateTime, LocalDateTime futureDateTime){
        return pastDateTime.isBefore(futureDateTime) || pastDateTime.equals(futureDateTime);
    }
}
