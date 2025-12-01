package com.taskflow.features.projects.dto;

import java.time.LocalDate;

import lombok.Data;

@Data	
public class MilestoneRequest {
	private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
