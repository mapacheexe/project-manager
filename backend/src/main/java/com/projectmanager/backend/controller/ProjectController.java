package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> findAll() {
        List<ProjectDTO> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> findById(@PathVariable Long id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> create(@RequestBody ProjectDTO request) {
        ProjectDTO projectDTO = projectService.create(request);
        return ResponseEntity.ok(projectDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody ProjectDTO request
    ) {
        return ResponseEntity.ok(projectService.update(id, request, requesterId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Id") Long requesterId) {
        projectService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }

}
