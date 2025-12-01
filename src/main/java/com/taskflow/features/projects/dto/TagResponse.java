package com.taskflow.features.projects.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {
	private Long id;
    private String name;
    private String color;
    private Long projectId;
}
