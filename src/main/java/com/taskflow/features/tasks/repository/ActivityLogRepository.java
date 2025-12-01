package com.taskflow.features.tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.features.tasks.model.ActivityLog;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>{
	List<ActivityLog> findByProjectIdOrderByTimestampDesc(Long projectId);
}
