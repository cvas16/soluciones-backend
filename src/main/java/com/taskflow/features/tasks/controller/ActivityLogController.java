package com.taskflow.features.tasks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.features.tasks.dto.ActivityLogResponse;
import com.taskflow.features.tasks.service.ActivityLogService;

@RestController
@RequestMapping("/api")
public class ActivityLogController {
	@Autowired
    private ActivityLogService activityLogService;

    // GET /api/projects/{projectId}/activity
    @GetMapping("/projects/{projectId}/activity")
    public ResponseEntity<List<ActivityLogResponse>> getProjectActivity(@PathVariable Long projectId) {
        return ResponseEntity.ok(activityLogService.getProjectActivity(projectId));
    }
}
