package com.projectmanager.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private List<Long> projectIds;
}
