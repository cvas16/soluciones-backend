package com.taskflow.features.tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.features.tasks.model.Dependency;

@Repository
public interface DependencyRepository extends JpaRepository<Dependency, Long>{
	List<Dependency> findByBlockedTaskId(Long blockedTaskId);
    List<Dependency> findByBlockerTaskId(Long blockerTaskId);
    
    boolean existsByBlockedTaskIdAndBlockerTaskId(Long blockedTaskId, Long blockerTaskId);
}
