package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.repository.UserProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
public class ProjectPermissionService {

    private final UserProjectRepository userProjectRepository;

    public ProjectPermissionService(UserProjectRepository userProjectRepository) {
        this.userProjectRepository = userProjectRepository;
    }

    public void requireProjectRole(Long projectId, Long userId, String... allowedRoles) {
        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "User has no access to project"));

        boolean allowed = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equalsIgnoreCase(userProject.getRole()));

        if (!allowed) {
            throw new ResponseStatusException(FORBIDDEN, "User has no permission for this action");
        }
    }
}
