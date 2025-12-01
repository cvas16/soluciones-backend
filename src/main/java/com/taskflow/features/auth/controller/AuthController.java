package com.taskflow.features.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.features.auth.dto.LoginRequest;
import com.taskflow.features.auth.dto.LoginResponse;
import com.taskflow.features.auth.dto.RegisterRequest;
import com.taskflow.features.auth.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest){
         authService.register(registerRequest);
         return ResponseEntity.ok(Map.of("message", "Usuario registrado exitosamente"));
	}
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", e.getMessage()));
        }
    }
	
}
