package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.repository.UserProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FORBIDDEN;

class ProjectPermissionServiceTest {

    private final UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
    private final ProjectPermissionService service = new ProjectPermissionService(userProjectRepository);

    @Test
    void allowsUserWithRequiredRole() {
        when(userProjectRepository.findByUserIdAndProjectId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(OWNER)));

        assertDoesNotThrow(() -> service.requireProjectRole(10L, 1L, OWNER, ADMIN));
    }

    @Test
    void rejectsUserWithoutProjectAccess() {
        when(userProjectRepository.findByUserIdAndProjectId(1L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.requireProjectRole(10L, 1L, OWNER)
        );

        assertEquals(FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void rejectsUserWithInsufficientRole() {
        when(userProjectRepository.findByUserIdAndProjectId(1L, 10L))
                .thenReturn(Optional.of(memberWithRole(MEMBER)));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.requireProjectRole(10L, 1L, OWNER, ADMIN)
        );

        assertEquals(FORBIDDEN, exception.getStatusCode());
    }

    private UserProject memberWithRole(String role) {
        UserProject userProject = new UserProject();
        userProject.setRole(role);
        return userProject;
    }
}
