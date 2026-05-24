package com.projectmanager.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDTO {
    private Long id;
    @NotBlank
    private String name;
    private List<Long> userIds;
    private List<StageDTO> stages;
}
