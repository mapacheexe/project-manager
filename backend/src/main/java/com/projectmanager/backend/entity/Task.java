package com.projectmanager.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String description;

    private String status;

    private Integer position;

    @ManyToOne
    @JoinColumn(name = "stage_id")
    private Stage stage;

}
