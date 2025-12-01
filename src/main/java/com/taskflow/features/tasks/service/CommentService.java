package com.taskflow.features.tasks.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.features.projects.model.Project;
import com.taskflow.features.tasks.dto.CommentRequest;
import com.taskflow.features.tasks.dto.CommentResponse;
import com.taskflow.features.tasks.model.Comment;
import com.taskflow.features.tasks.model.Task;
import com.taskflow.features.tasks.repository.CommentRepository;
import com.taskflow.features.tasks.repository.TaskRepository;
import com.taskflow.features.users.model.User;
import com.taskflow.features.users.repository.UserRepository;

@Service
public class CommentService {
	@Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long taskId, CommentRequest request, UserDetails userDetails) {
        User author = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        Project project = task.getProject();
        boolean isMember = project.getMembers().contains(author) || project.getOwner().equals(author);
        
        if (!isMember) {
            throw new RuntimeException("No tienes permiso para comentar en este proyecto");
        }

        Comment comment = Comment.builder()
                .text(request.getText())
                .task(task)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteComment(Long commentId, UserDetails userDetails) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comment.getAuthor().equals(currentUser)) {
            throw new RuntimeException("Solo puedes borrar tus propios comentarios");
        }
        
        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .authorId(comment.getAuthor().getId())
                .authorUsername(comment.getAuthor().getUsername())
                .build();
    }
}
