package com.taskflow.features.tasks.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.features.notifications.service.NotificationService;
import com.taskflow.features.projects.dto.TagResponse;
import com.taskflow.features.projects.model.Project;
import com.taskflow.features.projects.repository.ProjectRepository;
import com.taskflow.features.tasks.dto.TaskCreateRequest;
import com.taskflow.features.tasks.dto.TaskResponse;
import com.taskflow.features.tasks.model.Dependency;
import com.taskflow.features.tasks.model.Task;
import com.taskflow.features.tasks.repository.DependencyRepository;
import com.taskflow.features.tasks.repository.TaskRepository;
import com.taskflow.features.users.model.User;
import com.taskflow.features.users.repository.UserRepository;

@Service
public class TaskService {
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DependencyRepository dependencyRepository;
	@Autowired
    private ActivityLogService activityLogService;
	@Autowired
    private NotificationService notificationService;
	
	@Transactional
	public TaskResponse createTask(Long projectId, TaskCreateRequest request, UserDetails userDetails) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

		User currentUser = userRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		
		boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
		boolean isMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(currentUser.getId()));
		if (!isOwner && !isMember) {
			throw new RuntimeException("No tienes permiso para crear tareas en este proyecto");
		}
		
		User assignedUser = null;
		if (request.getAssignedUserId() != null) {
			assignedUser = userRepository.findById(request.getAssignedUserId()).orElse(null);
		}

		Task task = Task.builder().title(request.getTitle()).description(request.getDescription())
				.status(request.getStatus() != null ? request.getStatus() : "Pendiente")
				.priority(request.getPriority() != null ? request.getPriority() : "Media")
				.attachments(request.getAttachments()).project(project).assignedUser(assignedUser)
				.createdBy(currentUser).build();
		
		Task savedTask = taskRepository.save(task);
		activityLogService.logActivity(
	            savedTask.getProject(), 
	            currentUser,
	            savedTask, 
	            "TASK_CREATED", 
	            "Creó la tarea: " + savedTask.getTitle()
	        );
		return mapToResponse(savedTask);
	}

	@Transactional(readOnly = true)
	public List<TaskResponse> getTasksByProjectId(Long projectId) {
		return taskRepository.findAllByProjectId(projectId).stream().map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public TaskResponse updateTask(Long taskId, TaskCreateRequest request, UserDetails userDetails) {
		Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
		User currentUser = userRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("Usuario actual no encontrado"));
		//titulo
		if (request.getTitle() != null && !request.getTitle().equals(task.getTitle())) {
			activityLogService.logActivity(task.getProject(), currentUser, task, "TASK_UPDATED",
					"Cambió el título a: '" + request.getTitle() + "'");
			task.setTitle(request.getTitle());
		}

		// Descripción 
		if (request.getDescription() != null) {
			task.setDescription(request.getDescription());
		}

		//  Estado 
		if (request.getStatus() != null && !request.getStatus().equals(task.getStatus())) {
			if (request.getStatus().equals("Finalizado") || request.getStatus().equals("Hecho")) {
				List<Dependency> dependencies = dependencyRepository.findByBlockedTaskId(taskId);
				for (Dependency dep : dependencies) {
					Task blocker = dep.getBlockerTask();
					if (!blocker.getStatus().equals("Finalizado") && !blocker.getStatus().equals("Hecho")) {
						throw new RuntimeException(
								"No puedes finalizar esta tarea porque depende de: '" + blocker.getTitle() + "'");
					}
				}
			}
			
			activityLogService.logActivity(task.getProject(), currentUser, task, "STATUS_CHANGED",
					"Movió la tarea de '" + task.getStatus() + "' a '" + request.getStatus() + "'");
			
			task.setStatus(request.getStatus());
		}

		// Prioridad
		if (request.getPriority() != null && !request.getPriority().equals(task.getPriority())) {
			activityLogService.logActivity(task.getProject(), currentUser, task, "PRIORITY_CHANGED",
					"Cambió la prioridad a: " + request.getPriority());
			task.setPriority(request.getPriority());
		}

		// Adjuntos
		if (request.getAttachments() != null) {
			task.setAttachments(request.getAttachments());
		}

		//  Responsable
		if (request.getAssignedUserId() != null) {
			Long currentAssignedId = task.getAssignedUser() != null ? task.getAssignedUser().getId() : null;
			if (!request.getAssignedUserId().equals(currentAssignedId)) {
				if (!task.getProject().getOwner().getId().equals(currentUser.getId())) {
					throw new RuntimeException("Solo el dueño del proyecto puede asignar o reasignar tareas.");
				}
				User userToAssign = userRepository.findById(request.getAssignedUserId()).orElse(null);
				
				String newAssigneeName = userToAssign != null ? userToAssign.getUsername() : "Sin asignar";
				activityLogService.logActivity(task.getProject(), currentUser, task, "ASSIGNED_USER",
						"Asignó la tarea a: " + newAssigneeName);
				
				task.setAssignedUser(userToAssign);
				if (userToAssign != null && !userToAssign.getId().equals(currentUser.getId())) {
	                // Solo notificar si asigno a OTRA persona (no a mí mismo)
	                notificationService.createNotification(
	                    userToAssign, // Destinatario
	                    "Te han asignado la tarea: " + task.getTitle(), // Mensaje
	                    "ASSIGNMENT", // Tipo
	                    task.getId() 
	                );
	            }
			}
		}

		Task updatedTask = taskRepository.save(task);
		return mapToResponse(updatedTask);
	}

	@Transactional
    public void deleteTask(Long taskId, UserDetails userDetails) { 
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("Usuario intentando borrar: " + currentUser.getId() + " (" + currentUser.getUsername() + ")");
        System.out.println("Dueño del proyecto: " + task.getProject().getOwner().getId());
        
        if (task.getCreatedBy() != null) {
             System.out.println("Creador de la tarea: " + task.getCreatedBy().getId());
        } else {
             System.out.println("Creador de la tarea: NULL");
        }

        boolean isProjectOwner = task.getProject().getOwner().getId().equals(currentUser.getId());
        
        boolean isTaskCreator = false;
        if (task.getCreatedBy() != null) {
             isTaskCreator = task.getCreatedBy().getId().equals(currentUser.getId());
        }

        if (!isProjectOwner && !isTaskCreator) {
            throw new RuntimeException("No tienes permiso para eliminar esta tarea.");
        }
        
        taskRepository.delete(task);
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
				.milestoneId(task.getMilestone() != null ? task.getMilestone().getId() : null)
				.milestoneName(task.getMilestone() != null ? task.getMilestone().getName() : null)
				.tags(task.getTags().stream()
			        .map(tag -> TagResponse.builder()
			            .id(tag.getId())
			            .name(tag.getName())
			            .color(tag.getColor())
			            .projectId(tag.getProject().getId())
			            .build())
			        .collect(Collectors.toList()))
				.build();
	}
}
