package com.taskflow.features.notifications.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
	private Long id;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long relatedId;
}
