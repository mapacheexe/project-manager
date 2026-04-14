package com.projectmanager.backend.model;

import lombok.Data;

import java.util.Set;

@Data
public class ProjectDTO {

    private Long id;
    private String name;

    private Set<Long> userProjectsId;
}
