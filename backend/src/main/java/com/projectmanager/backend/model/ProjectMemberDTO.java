package com.projectmanager.backend.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectMemberDTO {
    private Long id;
    private Long userId;
    private Long projectId;
    private String role;
    private LocalDate joinedAt;
}
