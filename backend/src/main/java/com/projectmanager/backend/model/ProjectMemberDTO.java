package com.projectmanager.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectMemberDTO {
    private Long id;
    private Long userId;
    private Long projectId;
    @NotBlank
    private String role;
    private LocalDate joinedAt;
}
