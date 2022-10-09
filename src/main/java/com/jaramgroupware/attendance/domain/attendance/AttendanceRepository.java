package com.jaramgroupware.attendance.domain.attendance;

import com.jaramgroupware.attendance.domain.member.Member;
import com.jaramgroupware.attendance.domain.timeTable.TimeTable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,AttendanceID>, JpaSpecificationExecutor<Attendance>, AttendanceCustomRepository{

    @Query("SELECT a FROM ATTENDANCE a JOIN fetch a.timeTable JOIN fetch a.member WHERE (a.timeTable IN :timeTables) AND (a.member IN :members)")
    List<Attendance> findAttendancesIn(@Param("timeTables") List<TimeTable> timeTables, @Param("members") List<Member> members);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ATTENDANCE a WHERE (a.timeTable IN :timeTables) AND (a.member IN :members)")
    void deleteAllByIdInQuery(@Param("timeTables") List<TimeTable> timeTables, @Param("members") List<Member> members);

    Optional<List<Attendance>> findAllBy();
}
