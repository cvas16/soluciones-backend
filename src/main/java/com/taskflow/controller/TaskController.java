package com.taskflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.taskflow.dto.TaskCreateRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.service.TaskService;

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
	public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
		taskService.deleteTask(taskId);
		return ResponseEntity.noContent().build();
	}
}
