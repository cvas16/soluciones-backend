package com.taskflow.features.tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.features.tasks.model.TimeEntry;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long>{
	List<TimeEntry> findByTaskIdOrderByDateWorkedDesc(Long taskId);
    List<TimeEntry> findByUserId(Long userId);
}
