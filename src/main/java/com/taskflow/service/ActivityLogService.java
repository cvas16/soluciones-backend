package com.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.ActivityLogResponse;
import com.taskflow.model.ActivityLog;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.ActivityLogRepository;

@Service
public class ActivityLogService {
	@Autowired
    private ActivityLogRepository activityLogRepository;

    @Transactional
    public void logActivity(Project project, User user, Task task, String action, String description) {
        ActivityLog log = ActivityLog.builder()
                .project(project)
                .user(user)
                .task(task) 
                .action(action)
                .description(description)
                .build();
        
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getProjectActivity(Long projectId) {
        return activityLogRepository.findByProjectIdOrderByTimestampDesc(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ActivityLogResponse mapToResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .description(log.getDescription())
                .timestamp(log.getTimestamp())
                .userName(log.getUser().getUsername())
                .taskId(log.getTask() != null ? log.getTask().getId() : null)
                .build();
    }
}
