package com.taskflow.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taskflow.dto.ActivityLogResponse;
import com.taskflow.service.ActivityLogService;

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
