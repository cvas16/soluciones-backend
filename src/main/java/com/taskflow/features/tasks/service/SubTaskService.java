package com.taskflow.features.tasks.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.features.projects.model.Project;
import com.taskflow.features.tasks.dto.SubTaskRequest;
import com.taskflow.features.tasks.dto.SubTaskResponse;
import com.taskflow.features.tasks.model.SubTask;
import com.taskflow.features.tasks.model.Task;
import com.taskflow.features.tasks.repository.SubTaskRepository;
import com.taskflow.features.tasks.repository.TaskRepository;
import com.taskflow.features.users.model.User;
import com.taskflow.features.users.repository.UserRepository;

@Service
public class SubTaskService {
	@Autowired
    private SubTaskRepository subTaskRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public SubTaskResponse createSubTask(Long taskId, SubTaskRequest request, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        checkMemberAccess(task.getProject(), currentUser);

        SubTask subTask = SubTask.builder()
                .title(request.getTitle())
                .completed(false)
                .task(task)
                .build();

        return mapToResponse(subTaskRepository.save(subTask));
    }

    @Transactional
    public SubTaskResponse updateSubTask(Long subTaskId, SubTaskRequest request, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Sub-tarea no encontrada"));

        checkMemberAccess(subTask.getTask().getProject(), currentUser);

        if (request.getTitle() != null) subTask.setTitle(request.getTitle());
        if (request.getCompleted() != null) subTask.setCompleted(request.getCompleted());

        return mapToResponse(subTaskRepository.save(subTask));
    }

    @Transactional
    public void deleteSubTask(Long subTaskId, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        SubTask subTask = subTaskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Sub-tarea no encontrada"));

        checkMemberAccess(subTask.getTask().getProject(), currentUser);

        subTaskRepository.delete(subTask);
    }

    @Transactional(readOnly = true)
    public List<SubTaskResponse> getSubTasks(Long taskId) {
        return subTaskRepository.findByTaskIdOrderByIdAsc(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void checkMemberAccess(Project project, User user) {
        boolean isMember = project.getMembers().contains(user) || project.getOwner().equals(user);
        if (!isMember) {
            throw new RuntimeException("No tienes permiso en este proyecto");
        }
    }

    private SubTaskResponse mapToResponse(SubTask subTask) {
        return SubTaskResponse.builder()
                .id(subTask.getId())
                .title(subTask.getTitle())
                .completed(subTask.isCompleted())
                .taskId(subTask.getTask().getId())
                .build();
    }
}
