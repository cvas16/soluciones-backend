package com.taskflow.features.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.features.admin.dto.AdminUserResponse;
import com.taskflow.features.projects.repository.ProjectRepository;
import com.taskflow.features.users.model.Role;
import com.taskflow.features.users.model.User;
import com.taskflow.features.users.repository.RoleRepository;
import com.taskflow.features.users.repository.UserRepository;

@Service
public class AdminService {
	@Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .isLocked(!user.isAccountNonLocked()) 
                .projectCount(user.getProjects().size()) 
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public void toggleUserLock(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Invertir el estado de bloqueo
        user.setAccountNonLocked(!user.isAccountNonLocked());
        userRepository.save(user);
    }

    @Transactional
    public void toggleAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

        if (user.getRoles().contains(adminRole)) {
            user.getRoles().remove(adminRole); 
        } else {
            user.getRoles().add(adminRole); 
        }
        userRepository.save(user);
    }
    
    @Transactional
    public void forceDeleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }
}
