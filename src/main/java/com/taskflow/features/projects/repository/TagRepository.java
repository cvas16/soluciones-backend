package com.taskflow.features.projects.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskflow.features.projects.model.Tag;

@Repository
public interface TagRepository	extends JpaRepository<Tag, Long> {
	List<Tag> findByProjectId(Long projectId);
}
