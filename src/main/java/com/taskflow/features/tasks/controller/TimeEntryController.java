package com.taskflow.features.tasks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.taskflow.features.tasks.dto.TimeEntryRequest;
import com.taskflow.features.tasks.dto.TimeEntryResponse;
import com.taskflow.features.tasks.service.TimeEntryService;

@RestController
@RequestMapping("/api")
public class TimeEntryController {
	@Autowired private TimeEntryService timeEntryService;

    @GetMapping("/tasks/{taskId}/time")
    public ResponseEntity<List<TimeEntryResponse>> getTaskTime(@PathVariable Long taskId) {
        return ResponseEntity.ok(timeEntryService.getTaskTimeEntries(taskId));
    }

    @PostMapping("/tasks/{taskId}/time")
    public ResponseEntity<TimeEntryResponse> logTime(
            @PathVariable Long taskId,
            @RequestBody TimeEntryRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(timeEntryService.logTime(taskId, request, user));
    }

    @DeleteMapping("/time/{id}")
    public ResponseEntity<Void> deleteTimeEntry(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        timeEntryService.deleteTimeEntry(id, user);
        return ResponseEntity.noContent().build();
    }
}
