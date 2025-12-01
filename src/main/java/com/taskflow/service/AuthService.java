package com.taskflow.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.taskflow.model.Role;
import com.taskflow.model.User;
import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.LoginResponse;
import com.taskflow.dto.RegisterRequest;
import com.taskflow.repository.RoleRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtService;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
    private AuthenticationManager authenticationManager;
	@Autowired
    private JwtService jwtService;
	@Autowired 
	private RoleRepository roleRepository;
	
	public void register(RegisterRequest request) {
		if(userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new RuntimeException("Error: ¡El nombre del usuario ya esta en uso!");
		}
		if(userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("Error: ¡El email ya está en uso!");
		}
		Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
		
		User user = User.builder()
	            .username(request.getUsername())
	            .email(request.getEmail())
	            .password(passwordEncoder.encode(request.getPassword()))
	            .roles(Set.of(userRole))
	            .build();
		userRepository.save(user);
	}
	
	public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de la autenticación"));
            String token = jwtService.generateToken(user);
            return LoginResponse.builder()
            		.token(token)
            		.id(user.getId())
            		.username(user.getUsername())
            		.roles(user.getAuthorities().stream()
                            .map(auth -> auth.getAuthority())
                            .collect(Collectors.toList()))
            		.build();
	}
}
