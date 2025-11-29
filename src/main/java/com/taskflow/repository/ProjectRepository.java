package com.taskflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	List<Project> findAllByOwnerId(Long ownerId);
	List<Project> findByMembers_Id(Long userId);
}
