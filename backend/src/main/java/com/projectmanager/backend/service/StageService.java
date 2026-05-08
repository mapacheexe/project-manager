package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.Stage;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.StageDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.StageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final ProjectPermissionService permissionService;
    private final ProjectMapper projectMapper;

    public StageService(
            StageRepository stageRepository,
            ProjectRepository projectRepository,
            ProjectPermissionService permissionService,
            ProjectMapper projectMapper
    ) {
        this.stageRepository = stageRepository;
        this.projectRepository = projectRepository;
        this.permissionService = permissionService;
        this.projectMapper = projectMapper;
    }

    public List<StageDTO> findByProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(NOT_FOUND, "Project not found");
        }

        return stageRepository.findByProjectIdOrderByPositionAscIdAsc(projectId)
                .stream()
                .map(projectMapper::toStageDTO)
                .toList();
    }

    public StageDTO create(Long projectId, StageDTO request, Long requesterId) {
        permissionService.requireProjectRole(projectId, requesterId, OWNER, ADMIN);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found"));

        Stage stage = new Stage();
        stage.setName(request.getName());
        stage.setPosition(request.getPosition());
        stage.setProject(project);

        return projectMapper.toStageDTO(stageRepository.save(stage));
    }

    public StageDTO update(Long id, StageDTO request, Long requesterId) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Stage not found"));

        permissionService.requireProjectRole(stage.getProject().getId(), requesterId, OWNER, ADMIN);

        stage.setName(request.getName());
        stage.setPosition(request.getPosition());

        return projectMapper.toStageDTO(stageRepository.save(stage));
    }

    public void delete(Long id, Long requesterId) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Stage not found"));

        permissionService.requireProjectRole(stage.getProject().getId(), requesterId, OWNER, ADMIN);

        stageRepository.delete(stage);
    }

    public List<StageDTO> reorder(Long projectId, List<StageDTO> request, Long requesterId) {
        permissionService.requireProjectRole(projectId, requesterId, OWNER, ADMIN);

        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(NOT_FOUND, "Project not found");
        }

        request.forEach(stageDTO -> {
            Stage stage = stageRepository.findById(stageDTO.getId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Stage not found"));

            if (!stage.getProject().getId().equals(projectId)) {
                throw new ResponseStatusException(NOT_FOUND, "Stage not found in project");
            }

            stage.setPosition(stageDTO.getPosition());
            stageRepository.save(stage);
        });

        return findByProjectId(projectId);
    }

}
