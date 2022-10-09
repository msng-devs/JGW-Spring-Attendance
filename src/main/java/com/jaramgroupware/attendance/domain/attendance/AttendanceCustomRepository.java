package com.jaramgroupware.attendance.domain.attendance;

import java.util.List;

public interface AttendanceCustomRepository {

    void bulkInsert(List<Attendance> attendances,String who);
    void bulkUpdate(List<Attendance> attendances,String who);
}
