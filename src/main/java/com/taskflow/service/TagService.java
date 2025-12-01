package com.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.TagRequest;
import com.taskflow.dto.TagResponse;
import com.taskflow.model.Project;
import com.taskflow.model.Tag;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TagRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;

@Service
public class TagService {
	@Autowired
    private TagRepository tagRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;


    @Transactional
    public TagResponse createTag(Long projectId, TagRequest request, UserDetails userDetails) {
        User currentUser = getUser(userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        checkMemberAccess(project, currentUser);

        Tag tag = Tag.builder()
                .name(request.getName())
                .color(request.getColor() != null ? request.getColor() : "#808080")
                .project(project)
                .build();

        return mapToResponse(tagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByProject(Long projectId) {
        return tagRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTagToTask(Long taskId, Long tagId, UserDetails userDetails) {
        User currentUser = getUser(userDetails);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada"));

        checkMemberAccess(task.getProject(), currentUser);
        if (!tag.getProject().getId().equals(task.getProject().getId())) {
            throw new RuntimeException("La etiqueta no pertenece al proyecto de la tarea");
        }

        task.getTags().add(tag);
        taskRepository.save(task);
    }

    @Transactional
    public void removeTagFromTask(Long taskId, Long tagId, UserDetails userDetails) {
        User currentUser = getUser(userDetails);
        Task task = taskRepository.findById(taskId).orElseThrow();
        Tag tag = tagRepository.findById(tagId).orElseThrow();

        checkMemberAccess(task.getProject(), currentUser);

        task.getTags().remove(tag);
        taskRepository.save(task);
    }
    
    @Transactional
    public void deleteTag(Long tagId, UserDetails userDetails) {
        tagRepository.deleteById(tagId);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    private void checkMemberAccess(Project project, User user) {
        if (!project.getMembers().contains(user) && !project.getOwner().equals(user)) {
            throw new RuntimeException("Acceso denegado");
        }
    }

    private TagResponse mapToResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .projectId(tag.getProject().getId())
                .build();
    }
}
