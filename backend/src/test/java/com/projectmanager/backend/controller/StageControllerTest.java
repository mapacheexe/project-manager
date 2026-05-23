package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.StageDTO;
import com.projectmanager.backend.service.StageService;
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

class StageControllerTest {

    private final StageService stageService = mock(StageService.class);
    private final StageController controller = new StageController(stageService);
    private final Authentication authentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void givenExistingProject_whenFindByProjectId_thenReturnsOk() {
        StageDTO stage = new StageDTO();
        stage.setId(100L);
        when(stageService.findByProjectId(10L)).thenReturn(List.of(stage));

        ResponseEntity<List<StageDTO>> response = controller.findByProjectId(10L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(100L, response.getBody().get(0).getId());
    }

    @Test
    void givenValidRequest_whenCreate_thenReturnsOk() {
        StageDTO request = new StageDTO();
        request.setName("Todo");
        StageDTO created = new StageDTO();
        created.setId(100L);
        created.setName("Todo");
        when(stageService.create(10L, request, 1L)).thenReturn(created);

        ResponseEntity<StageDTO> response = controller.create(10L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Todo", response.getBody().getName());
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnsOk() {
        StageDTO request = new StageDTO();
        request.setName("Done");
        StageDTO updated = new StageDTO();
        updated.setId(100L);
        updated.setName("Done");
        when(stageService.update(100L, request, 1L)).thenReturn(updated);

        ResponseEntity<StageDTO> response = controller.update(100L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Done", response.getBody().getName());
    }

    @Test
    void givenExistingStage_whenDelete_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.delete(100L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(stageService).delete(100L, 1L);
    }

    @Test
    void givenValidRequest_whenReorder_thenReturnsOk() {
        StageDTO stageDTO = new StageDTO();
        stageDTO.setId(100L);
        List<StageDTO> request = List.of(stageDTO);
        when(stageService.reorder(10L, request, 1L)).thenReturn(request);

        ResponseEntity<List<StageDTO>> response = controller.reorder(10L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}
