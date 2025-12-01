package com.taskflow.features.tasks.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateRequest {
	private String title;
    private String description;
    private String status; 
    private Long assignedUserId;
    private String priority; 
    private List<String> attachments;
}
