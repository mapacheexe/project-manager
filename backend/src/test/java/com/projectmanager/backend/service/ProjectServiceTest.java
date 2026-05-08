package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class ProjectServiceTest {

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectPermissionService permissionService = mock(ProjectPermissionService.class);
    private final ProjectMapper projectMapper = new ProjectMapper();
    private final ProjectService service = new ProjectService(
            projectRepository,
            permissionService,
            projectMapper
    );

    @Test
    void createPersistsProject() {
        ProjectDTO request = new ProjectDTO();
        request.setName("Backend");
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDTO result = service.create(request);

        assertEquals("Backend", result.getName());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void updateChangesProjectNameWhenUserCanManageProject() {
        Project project = projectWithId(10L);
        ProjectDTO request = new ProjectDTO();
        request.setName("Updated");

        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDTO result = service.update(10L, request, 1L);

        assertEquals("Updated", result.getName());
        verify(projectRepository).save(project);
    }

    @Test
    void updateRejectsMissingProject() {
        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN);
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.update(10L, new ProjectDTO(), 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        verify(projectRepository, never()).save(any(Project.class));
    }

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        project.setName("Original");
        return project;
    }
}
