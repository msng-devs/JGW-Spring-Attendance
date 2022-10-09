package com.jaramgroupware.attendance.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findEventById(Long id);
    Optional<List<Event>> findAllBy();
}
