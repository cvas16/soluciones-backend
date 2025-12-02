package com.taskflow.features.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.features.users.dto.ChangePasswordRequest;
import com.taskflow.features.users.dto.UpdateProfileRequest;
import com.taskflow.features.users.dto.UserProfileResponse;
import com.taskflow.features.users.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
    private UserService userService;

    // GET /api/users/profile  
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserProfile(userDetails));
    }

    // PUT /api/users/profile
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        return ResponseEntity.ok(userService.updateProfile(request, userDetails));
    }

    // POST /api/users/password 
    @PostMapping("/password")
    public ResponseEntity<?> changePassword( 
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        try {
            userService.changePassword(request, userDetails);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
