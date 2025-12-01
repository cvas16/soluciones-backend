package com.taskflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.taskflow.dto.TagRequest;
import com.taskflow.dto.TagResponse;
import com.taskflow.service.TagService;

@RestController
@RequestMapping("/api")
public class TagController {
	@Autowired
    private TagService tagService;

    @PostMapping("/projects/{projectId}/tags")
    public ResponseEntity<TagResponse> createTag(
            @PathVariable Long projectId,
            @RequestBody TagRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(projectId, request, userDetails));
    }

    @GetMapping("/projects/{projectId}/tags")
    public ResponseEntity<List<TagResponse>> getTags(@PathVariable Long projectId) {
        return ResponseEntity.ok(tagService.getTagsByProject(projectId));
    }

    // POST /api/tasks/1/tags/5
    @PostMapping("/tasks/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToTask(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal UserDetails userDetails) {
        tagService.addTagToTask(taskId, tagId, userDetails);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/tasks/1/tags/5
    @DeleteMapping("/tasks/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromTask(
            @PathVariable Long taskId,
            @PathVariable Long tagId,
            @AuthenticationPrincipal UserDetails userDetails) {
        tagService.removeTagFromTask(taskId, tagId, userDetails);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId, @AuthenticationPrincipal UserDetails userDetails) {
        tagService.deleteTag(tagId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
