package com.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
	List<Task> findAllByProjectId(Long projectId);
    List<Task> findAllByAssignedUserId(Long userId);
}
