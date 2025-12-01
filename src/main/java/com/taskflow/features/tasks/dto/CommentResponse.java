package com.taskflow.features.tasks.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
	private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Long authorId;
    private String authorUsername;
}
