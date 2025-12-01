package com.taskflow.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.taskflow.dto.NotificationResponse;
import com.taskflow.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	@Autowired private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetails user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
}
