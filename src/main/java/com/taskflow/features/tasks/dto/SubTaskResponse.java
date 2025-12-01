package com.taskflow.features.tasks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskResponse {
	private Long id;
    private String title;
    private boolean completed;
    private Long taskId;
}
