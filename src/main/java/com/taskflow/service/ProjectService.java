package com.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.ProjectCreateRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.model.Project;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.UserRepository;

@Service
public class ProjectService {
	@Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    @Transactional(readOnly = true) 
    public List<ProjectResponse> getProjectsForUser(UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        List<Project> projects = projectRepository.findAllByOwnerId(user.getId());
        return projects.stream()
            .map(this::mapProjectToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional 
    public ProjectResponse createProject(ProjectCreateRequest request, UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        Project project = Project.builder()
            .name(request.getName())
            .description(request.getDescription())
            .owner(user)
            .build();
        Project savedProject = projectRepository.save(project);

        return mapProjectToResponse(savedProject);
    }
    @Transactional
    public void deleteProject(Long projectId, UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Acceso denegado: No eres el dueño de este proyecto");
        }
        projectRepository.delete(project);
    }
    
    public ProjectResponse getProjectById(Long projectId, UserDetails userDetails) {
    	User user = getUserFromDetails(userDetails);
    	Project project = projectRepository.findById(projectId)
    			.orElseThrow(()-> new RuntimeException("Proyecto no encontrado"));
    	if(!project.getOwner().getId().equals(user.getId())) {
    		throw new RuntimeException("No tienes permiso para ver este proyecto");
    	}
    	return mapProjectToResponse(project);
    }
    
    private ProjectResponse mapProjectToResponse(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .description(project.getDescription())
            .ownerUsername(project.getOwner().getUsername())
            .build();
    }
    
    @Transactional
    public void addMember(Long projectId, String usernameToInvite, UserDetails currentUserDetails) {
        User currentUser = getUserFromDetails(currentUserDetails);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Solo el dueño puede invitar miembros");
        }

        User userToInvite = userRepository.findByUsername(usernameToInvite)
            .orElseThrow(() -> new RuntimeException("Usuario a invitar no encontrado"));

        project.getMembers().add(userToInvite);
        
        projectRepository.save(project);
    }
    
    @Transactional
    public void removeMember(Long projectId, Long userIdToRemove, UserDetails currentUserDetails) {
        User currentUser = getUserFromDetails(currentUserDetails);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Solo el dueño puede eliminar miembros");
        }

        project.getMembers().removeIf(user -> user.getId().equals(userIdToRemove));
        projectRepository.save(project);
    }
}
