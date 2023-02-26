package com.jaramgroupware.attendance.domain.timeTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable,Long> , JpaSpecificationExecutor<TimeTable> {

    Optional<TimeTable> findTimeTableById(Long id);
    Optional<List<TimeTable>> findAllBy();

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM TIMETABLE t WHERE t.id = :id")
    void deleteTimeTableById(@Param("id") Long id);

    @Query(value = "SELECT MAX(t.TIMETABLE_START_DTTM) AS maxStartDateTime,MIN(t.TIMETABLE_START_DTTM) AS minStartDateTime,MAX(t.TIMETABLE_END_DTTM) AS maxEndDateTime,MIN(t.TIMETABLE_END_DTTM) AS minEndDateTime FROM TIMETABLE AS t WHERE t.EVENT_EVENT_PK = :id",nativeQuery = true)
    TimeTableDateTimes findTimeTableDateTimesByEventId(@Param(value = "id") Long id);
}
