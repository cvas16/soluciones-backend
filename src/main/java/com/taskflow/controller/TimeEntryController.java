package com.taskflow.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.taskflow.dto.TimeEntryRequest;
import com.taskflow.dto.TimeEntryResponse;
import com.taskflow.service.TimeEntryService;

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
