package com.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.MilestoneRequest;
import com.taskflow.dto.MilestoneResponse;
import com.taskflow.model.Milestone;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.MilestoneRepository;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

@Service
public class MilestoneService {
	@Autowired private MilestoneRepository milestoneRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public MilestoneResponse createMilestone(Long projectId, MilestoneRequest request, UserDetails userDetails) {
        User currentUser = getUser(userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        checkMemberAccess(project, currentUser);

        Milestone milestone = Milestone.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .project(project)
                .build();

        return mapToResponse(milestoneRepository.save(milestone));
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestonesByProject(Long projectId) {
        return milestoneRepository.findByProjectIdOrderByEndDateAsc(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void addTaskToMilestone(Long taskId, Long milestoneId, UserDetails userDetails) {
        User currentUser = getUser(userDetails);
        Task task = taskRepository.findById(taskId).orElseThrow();
        Milestone milestone = milestoneRepository.findById(milestoneId).orElseThrow();

        checkMemberAccess(task.getProject(), currentUser);
        
        if (!task.getProject().getId().equals(milestone.getProject().getId())) {
             throw new RuntimeException("La tarea y el hito deben ser del mismo proyecto");
        }

        task.setMilestone(milestone);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteMilestone(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId).orElseThrow();
        for(Task t : milestone.getTasks()) {
            t.setMilestone(null);
        }
        milestoneRepository.delete(milestone);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    private void checkMemberAccess(Project project, User user) {
        if (!project.getMembers().contains(user) && !project.getOwner().equals(user)) {
            throw new RuntimeException("Acceso denegado");
        }
    }

    private MilestoneResponse mapToResponse(Milestone m) {
        int totalTasks = m.getTasks().size();
        int completedTasks = (int) m.getTasks().stream()
                .filter(t -> "Hecho".equals(t.getStatus()) || "Finalizado".equals(t.getStatus()))
                .count();
        int progress = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;

        return MilestoneResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .description(m.getDescription())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .projectId(m.getProject().getId())
                .progress(progress)
                .build();
    }	
}
