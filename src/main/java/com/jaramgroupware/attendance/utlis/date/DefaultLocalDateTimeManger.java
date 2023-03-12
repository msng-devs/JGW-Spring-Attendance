package com.jaramgroupware.attendance.utlis.date;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class DefaultLocalDateTimeManger {
    private final Clock clock = Clock.systemDefaultZone();

    public LocalDateTime getNow(){
        return LocalDateTime.now(clock);
    }
}
