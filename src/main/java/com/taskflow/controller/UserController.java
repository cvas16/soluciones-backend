package com.taskflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.UserSummaryResponse;
import com.taskflow.service.ProjectService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
    private ProjectService projectService; 

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(projectService.searchUsers(query));
    }
}
