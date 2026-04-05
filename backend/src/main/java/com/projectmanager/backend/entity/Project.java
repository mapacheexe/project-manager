package com.projectmanager.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToMany
    private Set<User> users;
}
