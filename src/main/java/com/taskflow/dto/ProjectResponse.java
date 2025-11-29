package com.taskflow.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
	private Long id;
    private String name;
    private String description;
    private String ownerUsername;
    private String background;
    private List<UserSummaryResponse> members;
    private int taskCount;
    private int membersCount;
}
