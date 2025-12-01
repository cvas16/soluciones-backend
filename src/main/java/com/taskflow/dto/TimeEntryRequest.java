package com.taskflow.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TimeEntryRequest {
	private Integer durationMinutes;
    private String description;
    private LocalDateTime dateWorked;
}
