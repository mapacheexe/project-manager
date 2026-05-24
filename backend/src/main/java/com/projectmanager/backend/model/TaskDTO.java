package com.projectmanager.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    private Long stageId;
    @NotBlank
    private String title;
    private String description;
    private String status;
    private Integer position;
}
