package com.taskflow.features.users.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
	private String username;
    private String email;
}
