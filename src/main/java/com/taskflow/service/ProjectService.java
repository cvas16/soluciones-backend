package com.taskflow.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.taskflow.dto.UserSummaryResponse;
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
        List<Project> ownedProjects = projectRepository.findAllByOwnerId(user.getId());
        List<Project> memberProjects = projectRepository.findByMembers_Id(user.getId());
        Set<Project> allProjects = new HashSet<>();
        allProjects.addAll(ownedProjects);
        allProjects.addAll(memberProjects);
        return allProjects.stream()
                .map(this::mapProjectToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional 
    public ProjectResponse createProject(ProjectCreateRequest request, UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        Project project = Project.builder()
            .name(request.getName())
            .description(request.getDescription())
            .background(request.getBackground() != null ? request.getBackground() : "linear-gradient(135deg, #0052cc 0%, #2684ff 100%)")
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
    	boolean isOwner = project.getOwner().getId().equals(user.getId());
    	boolean isMember = project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(user.getId()));
    	if(!isOwner && !isMember){
    		throw new RuntimeException("Acceso denegado: No eres miembro de este proyecto");
    	}
    	return mapProjectToResponse(project);
    }
    
    private ProjectResponse mapProjectToResponse(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .description(project.getDescription())
            .ownerUsername(project.getOwner().getUsername())
            .background(project.getBackground())
            .members(project.getMembers().stream()
                    .map(user -> UserSummaryResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .build())
                    .toList())
            .taskCount(project.getTasks().size())
            .membersCount(project.getMembers().size() + 1)
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
        
        if (project.getMembers().contains(userToInvite) || project.getOwner().equals(userToInvite)) {
            throw new RuntimeException("El usuario ya pertenece al proyecto");
        }
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
    
    public List<UserSummaryResponse> searchUsers(String query,Long projectId) {
    	List<User> users;
        if (projectId != null) {
            users = userRepository.findUsersToInvite(query, projectId);
        } else {
            users = userRepository.findByUsernameContainingIgnoreCase(query);
        }

        return users.stream()
            .map(user -> UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build())
            .collect(Collectors.toList());
    }
}
