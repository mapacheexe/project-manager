package com.projectmanager.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany
    private Set<UserProject> userProjects;
}
