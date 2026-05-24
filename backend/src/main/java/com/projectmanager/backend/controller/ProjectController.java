package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody ProjectDTO request
    ) {
        return ResponseEntity.ok(projectService.update(id, request, requesterId(authentication)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        projectService.delete(id, requesterId(authentication));
        return ResponseEntity.noContent().build();
    }

    private Long requesterId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

}
