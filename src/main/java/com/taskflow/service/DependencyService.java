package com.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.DependencyRequest;
import com.taskflow.dto.DependencyResponse;
import com.taskflow.model.Dependency;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.DependencyRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

@Service
public class DependencyService {
	@Autowired
    private DependencyRepository dependencyRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public DependencyResponse addDependency(Long blockedTaskId, DependencyRequest request, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Task blockedTask = taskRepository.findById(blockedTaskId)
                .orElseThrow(() -> new RuntimeException("Tarea bloqueada no encontrada"));
        
        Task blockerTask = taskRepository.findById(request.getBlockerTaskId())
                .orElseThrow(() -> new RuntimeException("Tarea bloqueadora no encontrada"));

        if (blockedTask.getId().equals(blockerTask.getId())) {
            throw new RuntimeException("Una tarea no puede bloquearse a sí misma");
        }
        
        if (!blockedTask.getProject().getId().equals(blockerTask.getProject().getId())) {
             throw new RuntimeException("Las tareas deben pertenecer al mismo proyecto");
        }

        checkMemberAccess(blockedTask.getProject(), currentUser);

        if (dependencyRepository.existsByBlockedTaskIdAndBlockerTaskId(blockedTask.getId(), blockerTask.getId())) {
            throw new RuntimeException("Esta dependencia ya existe");
        }


        Dependency dependency = Dependency.builder()
                .blockedTask(blockedTask)
                .blockerTask(blockerTask)
                .build();

        return mapToResponse(dependencyRepository.save(dependency));
    }

    @Transactional
    public void removeDependency(Long dependencyId, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Dependency dependency = dependencyRepository.findById(dependencyId)
                .orElseThrow(() -> new RuntimeException("Dependencia no encontrada"));

        checkMemberAccess(dependency.getBlockedTask().getProject(), currentUser);

        dependencyRepository.delete(dependency);
    }

    @Transactional(readOnly = true)
    public List<DependencyResponse> getDependencies(Long taskId) {
        return dependencyRepository.findByBlockedTaskId(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void checkMemberAccess(Project project, User user) {
        boolean isMember = project.getMembers().contains(user) || project.getOwner().equals(user);
        if (!isMember) {
            throw new RuntimeException("No tienes permiso en este proyecto");
        }
    }

    private DependencyResponse mapToResponse(Dependency dep) {
        return DependencyResponse.builder()
                .id(dep.getId())
                .blockedTaskId(dep.getBlockedTask().getId())
                .blockerTaskId(dep.getBlockerTask().getId())
                .blockerTaskTitle(dep.getBlockerTask().getTitle())
                .blockerTaskStatus(dep.getBlockerTask().getStatus())
                .build();
    }
}
