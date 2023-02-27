package com.jaramgroupware.attendance.domain.attendance;

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
public interface AttendanceRepository extends JpaRepository<Attendance,Long>, JpaSpecificationExecutor<Attendance>{

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ATTENDANCE a WHERE a.id in :id")
    void deleteAttendanceById(@Param("id") Long id);

    Optional<List<Attendance>> findAllBy();

    List<Attendance> findAllByIdIn(Set<Long> ids);
}
