package com.taskflow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.dto.NotificationResponse;
import com.taskflow.model.Notification;
import com.taskflow.model.User;
import com.taskflow.repository.NotificationRepository;
import com.taskflow.repository.UserRepository;

@Service
public class NotificationService {
	@Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public void createNotification(User recipient, String message, String type, Long relatedId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .relatedId(relatedId)
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(UserDetails userDetails) {
        User user = getUser(userDetails);
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, UserDetails userDetails) {
        User user = getUser(userDetails);
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notif.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("No puedes modificar esta notificación");
        }

        notif.setRead(true);
        notificationRepository.save(notif);
    }
    
    @Transactional
    public void markAllAsRead(UserDetails userDetails) {
        User user = getUser(userDetails);
        List<Notification> notifs = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
        notifs.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifs);
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .relatedId(n.getRelatedId())
                .build();
    }
}
