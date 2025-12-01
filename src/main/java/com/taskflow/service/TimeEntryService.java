package com.taskflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.TimeEntryRequest;
import com.taskflow.dto.TimeEntryResponse;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TimeEntry;
import com.taskflow.model.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.TimeEntryRepository;
import com.taskflow.repository.UserRepository;

@Service
public class TimeEntryService {
	@Autowired private TimeEntryRepository timeEntryRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public TimeEntryResponse logTime(Long taskId, TimeEntryRequest request, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Task task = taskRepository.findById(taskId).orElseThrow();

        checkMemberAccess(task.getProject(), currentUser);

        TimeEntry entry = TimeEntry.builder()
                .durationMinutes(request.getDurationMinutes())
                .description(request.getDescription())
                .dateWorked(request.getDateWorked() != null ? request.getDateWorked() : LocalDateTime.now())
                .task(task)
                .user(currentUser)
                .build();

        return mapToResponse(timeEntryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<TimeEntryResponse> getTaskTimeEntries(Long taskId) {
        return timeEntryRepository.findByTaskIdOrderByDateWorkedDesc(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteTimeEntry(Long entryId, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        TimeEntry entry = timeEntryRepository.findById(entryId).orElseThrow();
        
        boolean isCreator = entry.getUser().getId().equals(currentUser.getId());
        boolean isProjectOwner = entry.getTask().getProject().getOwner().getId().equals(currentUser.getId());
        
        if (!isCreator && !isProjectOwner) {
            throw new RuntimeException("No puedes borrar este registro de tiempo");
        }
        
        timeEntryRepository.delete(entry);
    }

    private void checkMemberAccess(Project project, User user) {
        if (!project.getMembers().contains(user) && !project.getOwner().equals(user)) {
            throw new RuntimeException("Acceso denegado");
        }
    }

    private TimeEntryResponse mapToResponse(TimeEntry entry) {
        return TimeEntryResponse.builder()
                .id(entry.getId())
                .durationMinutes(entry.getDurationMinutes())
                .description(entry.getDescription())
                .dateWorked(entry.getDateWorked())
                .username(entry.getUser().getUsername())
                .userAvatarInitial(entry.getUser().getUsername().substring(0, 1).toUpperCase())
                .build();
    }
}
