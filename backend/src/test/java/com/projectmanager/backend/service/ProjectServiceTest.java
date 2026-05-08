package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

    @Test
    void findAllReturnsAllProjects() {
        Project first = projectWithId(10L);
        Project second = projectWithId(20L);
        when(projectRepository.findAll()).thenReturn(List.of(first, second));

        var result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void findByIdReturnsProjectIfExists() {
        Project project = projectWithId(10L);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        var result = service.findById(10L);

        assertEquals(10L, result.get().getId());
    }

    @Test
    void deleteRemovesExistingProject() {
        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER);
        when(projectRepository.existsById(10L)).thenReturn(true);

        service.delete(10L, 1L);

        verify(projectRepository).deleteById(10L);
    }

    @Test
    void deleteRejectsMissingProject() {
        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER);
        when(projectRepository.existsById(10L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.delete(10L, 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        verify(projectRepository, never()).deleteById(10L);
    }

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        project.setName("Original");
        return project;
    }
}
