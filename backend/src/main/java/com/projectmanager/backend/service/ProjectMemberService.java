package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.ProjectMemberDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import com.projectmanager.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectPermissionService permissionService;
    private final ProjectMapper projectMapper;

    public ProjectMemberService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            UserProjectRepository userProjectRepository,
            ProjectPermissionService permissionService,
            ProjectMapper projectMapper
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.userProjectRepository = userProjectRepository;
        this.permissionService = permissionService;
        this.projectMapper = projectMapper;
    }

    public List<ProjectMemberDTO> findMembers(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(NOT_FOUND, "Project not found");
        }

        return userProjectRepository.findByProjectId(projectId)
                .stream()
                .map(projectMapper::toProjectMemberDTO)
                .toList();
    }

    public ProjectMemberDTO addMember(Long projectId, Long userId, Long requesterId) {
        permissionService.requireProjectRole(projectId, requesterId, OWNER, ADMIN);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project not found"));

        if (userProjectRepository.existsByUserIdAndProjectId(userId, projectId)) {
            throw new ResponseStatusException(CONFLICT, "User already assigned to project");
        }

        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setRole(MEMBER);
        userProject.setJoinedAt(LocalDate.now());

        return projectMapper.toProjectMemberDTO(userProjectRepository.save(userProject));
    }

    public ProjectMemberDTO updateMember(Long projectId, Long userId, ProjectMemberDTO request, Long requesterId) {
        permissionService.requireProjectRole(projectId, requesterId, OWNER, ADMIN);

        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project member not found"));

        userProject.setRole(request.getRole());

        return projectMapper.toProjectMemberDTO(userProjectRepository.save(userProject));
    }

    public void removeMember(Long projectId, Long userId, Long requesterId) {
        permissionService.requireProjectRole(projectId, requesterId, OWNER, ADMIN);

        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Project member not found"));

        userProjectRepository.delete(userProject);
    }
}
