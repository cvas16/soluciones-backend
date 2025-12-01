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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.features.tasks.dto.DependencyRequest;
import com.taskflow.features.tasks.dto.DependencyResponse;
import com.taskflow.features.tasks.service.DependencyService;

@RestController
@RequestMapping("/api")
public class DependencyController {
	@Autowired
    private DependencyService dependencyService;

    @GetMapping("/tasks/{taskId}/dependencies")
    public ResponseEntity<List<DependencyResponse>> getDependencies(@PathVariable Long taskId) {
        return ResponseEntity.ok(dependencyService.getDependencies(taskId));
    }

    @PostMapping("/tasks/{taskId}/dependencies")
    public ResponseEntity<DependencyResponse> addDependency(
            @PathVariable Long taskId,
            @RequestBody DependencyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dependencyService.addDependency(taskId, request, userDetails));
    }

    @DeleteMapping("/dependencies/{id}")
    public ResponseEntity<Void> removeDependency(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        dependencyService.removeDependency(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
