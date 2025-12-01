package com.taskflow.features.tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.features.tasks.model.SubTask;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long>{
	List<SubTask> findByTaskIdOrderByIdAsc(Long taskId);
}
