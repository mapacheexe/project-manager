package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public ResponseEntity<Project> saveProject(Project project) {

        return ResponseEntity.ok(projectRepository.save(project));
    }
}
