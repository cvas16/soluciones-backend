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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.ProjectCreateRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
	@Autowired
    private ProjectService projectService;
	/**
	*@AuthenticationPrincipal
	*/
	@GetMapping
    public ResponseEntity<List<ProjectResponse>> getUserProjects(@AuthenticationPrincipal UserDetails userDetails) {
        List<ProjectResponse> projects = projectService.getProjectsForUser(userDetails);
        return ResponseEntity.ok(projects);
    }
	
	@PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestBody ProjectCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        ProjectResponse createdProject = projectService.createProject(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }
	
	/**
	 * @PatchVariable
	 */
	@DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        try {
            projectService.deleteProject(id, userDetails);
            return ResponseEntity.noContent().build(); 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
        }
    }
	
	@PostMapping("/{id}/members")
    public ResponseEntity<Void> inviteMember(
            @PathVariable Long id,
            @RequestParam String username,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        projectService.addMember(id, username, userDetails);
        return ResponseEntity.ok().build();
    }
	
	@DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        projectService.removeMember(id, userId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
