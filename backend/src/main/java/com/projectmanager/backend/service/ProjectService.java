package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static com.projectmanager.backend.model.ProjectRole.OWNER;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectPermissionService permissionService;
    private final ProjectMapper projectMapper;

    public ProjectService(
            ProjectRepository projectRepository,
            UserProjectRepository userProjectRepository,
            ProjectPermissionService permissionService,
            ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.userProjectRepository = userProjectRepository;
        this.permissionService = permissionService;
        this.projectMapper = projectMapper;
    }

    public List<ProjectDTO> findAll(Long requesterId) {
        return userProjectRepository.findByUserId(requesterId)
                .stream()
                .map(up -> projectMapper.toProjectDTO(up.getProject()))
                .toList();
    }

    public Optional<ProjectDTO> findById(Long id, Long requesterId) {
        permissionService.requireProjectRole(id, requesterId, OWNER, ADMIN, MEMBER);
        return projectRepository.findById(id).map(projectMapper::toProjectDTO);
    }

    public ProjectDTO update(Long id, ProjectDTO request, Long requesterId) {
        permissionService.requireProjectRole(id, requesterId, OWNER, ADMIN);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found"));

        project.setName(request.getName());

        return projectMapper.toProjectDTO(projectRepository.save(project));
    }

    public void delete(Long id, Long requesterId) {
        permissionService.requireProjectRole(id, requesterId, OWNER);

        if (!projectRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Project not found");
        }

        projectRepository.deleteById(id);
    }

}
