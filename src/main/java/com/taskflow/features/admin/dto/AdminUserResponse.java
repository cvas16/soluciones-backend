package com.taskflow.features.admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder           
@NoArgsConstructor 
@AllArgsConstructor
public class AdminUserResponse {
	private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private boolean isLocked;  
    private int projectCount;
}
