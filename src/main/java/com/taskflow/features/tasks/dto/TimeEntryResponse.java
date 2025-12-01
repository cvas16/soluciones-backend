package com.taskflow.features.tasks.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeEntryResponse {
	private Long id;
    private Integer durationMinutes;
    private String description;
    private LocalDateTime dateWorked;
    private String username; 
    private String userAvatarInitial;
}	
