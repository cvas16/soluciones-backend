package com.taskflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.TaskCreateRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
	@Autowired
	private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Transactional	
    public TaskResponse createTask(Long projectId, TaskCreateRequest request, UserDetails userDetails) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getId().equals(currentUser.getId()));
        if (!isOwner && !isMember) {
            throw new RuntimeException("No tienes permiso para crear tareas en este proyecto");
        }
        
        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElse(null); 
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : "Pendiente")
                .priority(request.getPriority()!= null ? request.getPriority() : "Media")
                .attachments(request.getAttachments())
                .project(project)
                .assignedUser(assignedUser)
                .createdBy(currentUser)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        return taskRepository.findAllByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskCreateRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getAttachments() != null) task.setAttachments(request.getAttachments());
        if (request.getAssignedUserId() != null) {
            User user = userRepository.findById(request.getAssignedUserId()).orElse(null);
            task.setAssignedUser(user);
        }

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Tarea no encontrada");
        }
        taskRepository.deleteById(taskId);
    }
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .createdByUsername(task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : "Desconocido") 
                .attachments(task.getAttachments())
                .projectId(task.getProject().getId())
                .assignedUserId(task.getAssignedUser() != null ? task.getAssignedUser().getId() : null)
                .assignedUsername(task.getAssignedUser() != null ? task.getAssignedUser().getUsername() : null)
                .build();
    }
}
