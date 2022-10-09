package com.jaramgroupware.attendance.domain.attendanceType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceTypeRepository extends JpaRepository<AttendanceType,Integer> , JpaSpecificationExecutor<AttendanceType> {

    Optional<AttendanceType> findAttendanceTypeById(Integer id);
    Optional<List<AttendanceType>> findAllBy();
}
