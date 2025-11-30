package com.taskflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.taskflow.dto.SubTaskRequest;
import com.taskflow.dto.SubTaskResponse;
import com.taskflow.service.SubTaskService;

@RestController
@RequestMapping("/api")
public class SubTaskController {
	@Autowired
    private SubTaskService subTaskService;

    @GetMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<List<SubTaskResponse>> getSubTasks(@PathVariable Long taskId) {
        return ResponseEntity.ok(subTaskService.getSubTasks(taskId));
    }

    @PostMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<SubTaskResponse> createSubTask(
            @PathVariable Long taskId,
            @RequestBody SubTaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subTaskService.createSubTask(taskId, request, userDetails));
    }

    @PutMapping("/subtasks/{id}")
    public ResponseEntity<SubTaskResponse> updateSubTask(
            @PathVariable Long id,
            @RequestBody SubTaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        return ResponseEntity.ok(subTaskService.updateSubTask(id, request, userDetails));
    }

    @DeleteMapping("/subtasks/{id}")
    public ResponseEntity<Void> deleteSubTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        subTaskService.deleteSubTask(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
