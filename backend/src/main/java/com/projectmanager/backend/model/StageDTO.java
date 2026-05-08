package com.projectmanager.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class StageDTO {
    private Long id;
    private String name;
    private Long projectId;
    private Integer position;
    private List<TaskDTO> tasks;
}
