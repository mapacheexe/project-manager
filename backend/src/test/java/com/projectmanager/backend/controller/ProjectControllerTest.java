package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectControllerTest {

    private final ProjectService projectService = mock(ProjectService.class);
    private final ProjectController controller = new ProjectController(projectService);
    private final Authentication authentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void givenProjects_whenFindAll_thenReturnsOk() {
        ProjectDTO project = new ProjectDTO();
        project.setId(10L);
        when(projectService.findAll()).thenReturn(List.of(project));

        ResponseEntity<List<ProjectDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(10L, response.getBody().get(0).getId());
    }

    @Test
    void givenExistingProject_whenFindById_thenReturnsOk() {
        ProjectDTO project = new ProjectDTO();
        project.setId(10L);
        when(projectService.findById(10L)).thenReturn(Optional.of(project));

        ResponseEntity<ProjectDTO> response = controller.findById(10L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    void givenMissingProject_whenFindById_thenReturnsNotFound() {
        when(projectService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<ProjectDTO> response = controller.findById(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnsOk() {
        ProjectDTO request = new ProjectDTO();
        request.setName("Updated");
        ProjectDTO updated = new ProjectDTO();
        updated.setId(10L);
        updated.setName("Updated");
        when(projectService.update(10L, request, 1L)).thenReturn(updated);

        ResponseEntity<ProjectDTO> response = controller.update(10L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getName());
    }

    @Test
    void givenExistingProject_whenDelete_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete(10L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(projectService).delete(10L, 1L);
    }
}
