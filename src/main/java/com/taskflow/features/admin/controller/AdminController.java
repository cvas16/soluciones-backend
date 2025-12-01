package com.taskflow.features.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.features.admin.dto.AdminUserResponse;
import com.taskflow.features.admin.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	@Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/lock")
    public ResponseEntity<Void> toggleUserLock(@PathVariable Long id) {
        adminService.toggleUserLock(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Void> toggleAdminRole(@PathVariable Long id) {
        adminService.toggleAdminRole(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> forceDeleteProject(@PathVariable Long id) {
        adminService.forceDeleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
