package com.taskflow.features.tasks.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DependencyResponse {
	private Long id;
    private Long blockedTaskId;  
    private Long blockerTaskId;  
    private String blockerTaskTitle; 
    private String blockerTaskStatus;
}
