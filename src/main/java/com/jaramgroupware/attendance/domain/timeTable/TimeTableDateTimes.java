package com.jaramgroupware.attendance.domain.timeTable;

import java.time.LocalDateTime;

public interface TimeTableDateTimes {
    LocalDateTime getMaxStartDateTime();
    LocalDateTime getMinStartDateTime();
    LocalDateTime getMaxEndDateTime();
    LocalDateTime getMinEndDateTime();
}
