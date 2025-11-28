package com.taskflow.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
	private Long id;
    private String title;
    private String description;
    private String status;
    private Long projectId; 
    private Long assignedUserId;  
    private String assignedUsername;
    private String priority;
    private LocalDateTime createdAt;
    private String createdByUsername; 
    private List<String> attachments;
}
