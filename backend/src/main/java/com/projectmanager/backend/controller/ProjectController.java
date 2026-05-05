package com.projectmanager.backend.controller;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> findAll() {
        List<ProjectDTO> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectDTO> findById(@PathVariable Long id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO request DTO
    @PostMapping
    public ResponseEntity<ProjectDTO> save(@RequestBody Project project) {
        ProjectDTO projectDTO = projectService.save(project);
        return ResponseEntity.ok(projectDTO);
    }

}
