package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.StageDTO;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Optional<List<ProjectDTO>> findAll() {
        return Optional.of(
                projectRepository.findAll()
                        .stream().map(this::toDTO).toList()
        );
    }

    public Optional<ProjectDTO> findById(Long id) {
        return projectRepository.findById(id).map(this::toDTO);
    }

    public ProjectDTO save(Project project) {
        return toDTO(projectRepository.save(project));
    }

    private ProjectDTO toDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();

        dto.setId(project.getId());
        dto.setName(project.getName());

        dto.setUserIds(
                project.getUserProjects()
                        .stream()
                        .map(up -> up.getUser().getId())
                        .toList()
        );


        dto.setStages(
                project.getStages()
                        .stream()
                        .map(stage -> {
                            StageDTO stageDTO = new StageDTO();
                            stageDTO.setId(stage.getId());
                            stageDTO.setName(stage.getName());
                            return stageDTO;
                        })
                        .toList()
        );

        return dto;
    }


}
