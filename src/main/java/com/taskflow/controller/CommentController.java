package com.taskflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.dto.CommentRequest;
import com.taskflow.dto.CommentResponse;
import com.taskflow.service.CommentService;

@RestController
@RequestMapping("/api")
public class CommentController {
	@Autowired
    private CommentService commentService;

    // GET /api/tasks/{taskId}/comments
    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }

    // POST /api/tasks/{taskId}/comments
    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long taskId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) 
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(taskId, request, userDetails));
    }
    
    // DELETE /api/comments/{id}
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        commentService.deleteComment(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
