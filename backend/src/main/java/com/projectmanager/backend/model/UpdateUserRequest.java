package com.projectmanager.backend.model;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
}
