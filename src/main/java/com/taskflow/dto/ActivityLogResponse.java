package com.taskflow.dto;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityLogResponse {
	private Long id;
    private String action;
    private String description;
    private LocalDateTime timestamp;
    private String userName;
    private Long taskId;
}
