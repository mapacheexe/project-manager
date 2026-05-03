package com.projectmanager.backend.model;

import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer position;
}
