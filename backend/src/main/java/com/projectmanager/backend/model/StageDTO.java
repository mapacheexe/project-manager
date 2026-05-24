package com.projectmanager.backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class StageDTO {
    private Long id;
    @NotBlank
    private String name;
    private Long projectId;
    private Integer position;
    private List<TaskDTO> tasks;
}
