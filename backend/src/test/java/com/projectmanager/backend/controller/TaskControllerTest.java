package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.TaskDTO;
import com.projectmanager.backend.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskControllerTest {

    private final TaskService taskService = mock(TaskService.class);
    private final TaskController controller = new TaskController(taskService);
    private final Authentication authentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void givenExistingStage_whenFindByStageId_thenReturnsOk() {
        TaskDTO task = new TaskDTO();
        task.setId(200L);
        when(taskService.findByStageId(100L, 1L)).thenReturn(List.of(task));

        ResponseEntity<List<TaskDTO>> response = controller.findByStageId(100L, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(200L, response.getBody().get(0).getId());
    }

    @Test
    void givenValidRequest_whenCreate_thenReturnsOk() {
        TaskDTO request = new TaskDTO();
        request.setTitle("Fix bug");
        TaskDTO created = new TaskDTO();
        created.setId(200L);
        created.setTitle("Fix bug");
        when(taskService.create(100L, request, 1L)).thenReturn(created);

        ResponseEntity<TaskDTO> response = controller.create(100L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Fix bug", response.getBody().getTitle());
    }

    @Test
    void givenExistingTask_whenFindById_thenReturnsOk() {
        TaskDTO task = new TaskDTO();
        task.setId(200L);
        when(taskService.findById(200L, 1L)).thenReturn(task);

        ResponseEntity<TaskDTO> response = controller.findById(200L, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(200L, response.getBody().getId());
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnsOk() {
        TaskDTO request = new TaskDTO();
        request.setTitle("Updated");
        TaskDTO updated = new TaskDTO();
        updated.setId(200L);
        updated.setTitle("Updated");
        when(taskService.update(200L, request, 1L)).thenReturn(updated);

        ResponseEntity<TaskDTO> response = controller.update(200L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getTitle());
    }

    @Test
    void givenValidRequest_whenMove_thenReturnsOk() {
        TaskDTO request = new TaskDTO();
        request.setStageId(101L);
        request.setPosition(2);
        TaskDTO moved = new TaskDTO();
        moved.setId(200L);
        moved.setStageId(101L);
        when(taskService.move(200L, request, 1L)).thenReturn(moved);

        ResponseEntity<TaskDTO> response = controller.move(200L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(101L, response.getBody().getStageId());
    }

    @Test
    void givenExistingTask_whenDelete_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete(200L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(taskService).delete(200L, 1L);
    }
}
