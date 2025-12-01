package com.taskflow.features.projects.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.features.projects.dto.MilestoneRequest;
import com.taskflow.features.projects.dto.MilestoneResponse;
import com.taskflow.features.tasks.service.MilestoneService;

@RestController
@RequestMapping("/api")
public class MilestoneController {
	@Autowired private MilestoneService milestoneService;

    @PostMapping("/projects/{projectId}/milestones")
    public ResponseEntity<MilestoneResponse> createMilestone(
            @PathVariable Long projectId,
            @RequestBody MilestoneRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(milestoneService.createMilestone(projectId, request, user));
    }

    @GetMapping("/projects/{projectId}/milestones")
    public ResponseEntity<List<MilestoneResponse>> getMilestones(@PathVariable Long projectId) {
        return ResponseEntity.ok(milestoneService.getMilestonesByProject(projectId));
    }

    // PUT /api/tasks/1/milestone/5
    @PutMapping("/tasks/{taskId}/milestone/{milestoneId}")
    public ResponseEntity<Void> addTaskToMilestone(
            @PathVariable Long taskId,
            @PathVariable Long milestoneId,
            @AuthenticationPrincipal UserDetails user) {
        milestoneService.addTaskToMilestone(taskId, milestoneId, user);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/milestones/{id}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
        milestoneService.deleteMilestone(id);
        return ResponseEntity.noContent().build();
    }
}
