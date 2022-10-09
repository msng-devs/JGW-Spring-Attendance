package com.jaramgroupware.attendance.domain.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class AttendanceCustomRepositoryImpl implements AttendanceCustomRepository {

    private final JdbcTemplate jdbcTemplate;

//TODO pk 생성 방식을 Table로 수정하고, 다시 시도해보기

    @Override
    public void bulkInsert(List<Attendance> attendances,String who) {
        String sql = "INSERT INTO ATTENDANCE (ATTENDANCE_TYPE_ATTENDANCE_TYPE_PK, MEMBER_MEMBER_PK, ATTENDANCE_MODIFIED_DTTM, ATTENDANCE_CREATED_DTTM, TIMETABLE_TIMETABLE_PK, ATTENDANCE_INDEX,ATTENDANCE_CREATED_BY,ATTENDANCE_MODIFIED_BY) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Attendance attendance = attendances.get(i);
                ps.setInt(1,attendance.getAttendanceType().getId());
                ps.setString(2,attendance.getMember().getId());
                ps.setObject(3, LocalDateTime.now());
                ps.setObject(4, LocalDateTime.now());
                ps.setLong(5,attendance.getTimeTable().getId());
                ps.setString(6,attendance.getIndex());
                ps.setString(7,who);
                ps.setString(8,who);
            }

            @Override
            public int getBatchSize() {
                return attendances.size();
            }
        });
        attendances.clear();
    }


    @Override
    public void bulkUpdate(List<Attendance> attendances, String who) {
        String sql = "UPDATE `ATTENDANCE`"
                + " SET `ATTENDANCE_TYPE_ATTENDANCE_TYPE_PK` = (?), `ATTENDANCE_INDEX` = (?), `ATTENDANCE_MODIFIED_BY` = (?), `ATTENDANCE_MODIFIED_DTTM` = (?)"
                + " WHERE `MEMBER_MEMBER_PK` = (?) AND `TIMETABLE_TIMETABLE_PK` = (?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Attendance attendance = attendances.get(i);
                ps.setInt(1,attendance.getAttendanceType().getId());
                ps.setString(2,attendance.getIndex());
                ps.setString(3,who);
                ps.setObject(4,LocalDateTime.now());
                ps.setString(5,attendance.getMember().getId());
                ps.setLong(6,attendance.getTimeTable().getId());
            }

            @Override
            public int getBatchSize() {
                return attendances.size();
            }
        });
        attendances.clear();
    }

}
