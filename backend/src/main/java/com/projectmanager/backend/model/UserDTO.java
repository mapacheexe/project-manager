package com.projectmanager.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    Long id;
    String name;
    List<Long> projectIds;
}
