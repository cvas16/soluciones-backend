package com.taskflow.features.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.features.users.dto.ChangePasswordRequest;
import com.taskflow.features.users.dto.UpdateProfileRequest;
import com.taskflow.features.users.dto.UserProfileResponse;
import com.taskflow.features.users.model.User;
import com.taskflow.features.users.repository.UserRepository;

@Service
public class UserService {
	@Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public UserProfileResponse getUserProfile(UserDetails userDetails) {
        User user = getUser(userDetails);
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getName())
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request, UserDetails userDetails) {
        User user = getUser(userDetails);


        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        
        return UserProfileResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, UserDetails userDetails) {
        User user = getUser(userDetails);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) { 
             throw new RuntimeException("Las nuevas contraseñas no coinciden.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
