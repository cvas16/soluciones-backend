package com.taskflow.features.tasks.controller;

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

import com.taskflow.features.tasks.dto.TaskCreateRequest;
import com.taskflow.features.tasks.dto.TaskResponse;
import com.taskflow.features.tasks.service.TaskService;

@RestController
@RequestMapping("/api")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@PostMapping("/projects/{projectId}/tasks")
	public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId, @RequestBody TaskCreateRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, request, userDetails));
	}

	@GetMapping("/projects/{projectId}/tasks")
	public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId) {
		return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
	}
	
	@PutMapping("/tasks/{taskId}")
	public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody TaskCreateRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
			return ResponseEntity.ok(taskService.updateTask(taskId, request, userDetails));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId, 
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        taskService.deleteTask(taskId, userDetails);
        return ResponseEntity.noContent().build();
    
	}
}
