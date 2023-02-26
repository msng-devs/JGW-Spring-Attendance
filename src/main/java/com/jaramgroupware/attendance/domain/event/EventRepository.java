package com.jaramgroupware.attendance.domain.event;

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
public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findEventById(Long id);
    Optional<List<Event>> findAllBy();

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM EVENT e WHERE e.id = :id")
    void deleteEventById(@Param("id") Long id);
}
