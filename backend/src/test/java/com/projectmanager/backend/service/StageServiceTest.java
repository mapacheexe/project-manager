package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.Stage;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.StageDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static com.projectmanager.backend.model.ProjectRole.ADMIN;

class StageServiceTest {

    private final StageRepository stageRepository = mock(StageRepository.class);
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final ProjectPermissionService permissionService = mock(ProjectPermissionService.class);
    private final ProjectMapper projectMapper = new ProjectMapper();
    private final StageService service = new StageService(
            stageRepository,
            projectRepository,
            permissionService,
            projectMapper
    );

    @Test
    void createAddsStageToProject() {
        StageDTO request = new StageDTO();
        request.setName("Todo");
        request.setPosition(1);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(projectRepository.findById(10L)).thenReturn(Optional.of(projectWithId(10L)));
        when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StageDTO result = service.create(10L, request, 1L);

        assertEquals("Todo", result.getName());
        assertEquals(1, result.getPosition());
        assertEquals(10L, result.getProjectId());
        verify(stageRepository).save(any(Stage.class));
    }

    @Test
    void reorderUpdatesStagePositions() {
        Stage firstStage = stageWithProject(100L, 10L);
        Stage secondStage = stageWithProject(101L, 10L);
        StageDTO firstRequest = stageRequest(100L, 2);
        StageDTO secondRequest = stageRequest(101L, 1);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(projectRepository.existsById(10L)).thenReturn(true);
        when(stageRepository.findById(100L)).thenReturn(Optional.of(firstStage));
        when(stageRepository.findById(101L)).thenReturn(Optional.of(secondStage));
        when(stageRepository.findByProjectIdOrderByPositionAscIdAsc(10L)).thenReturn(List.of(secondStage, firstStage));

        List<StageDTO> result = service.reorder(10L, List.of(firstRequest, secondRequest), 1L);

        assertEquals(2, firstStage.getPosition());
        assertEquals(1, secondStage.getPosition());
        assertEquals(2, result.size());
        verify(stageRepository).save(firstStage);
        verify(stageRepository).save(secondStage);
    }

    @Test
    void reorderRejectsStageFromAnotherProject() {
        Stage stage = stageWithProject(100L, 20L);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(projectRepository.existsById(10L)).thenReturn(true);
        when(stageRepository.findById(100L)).thenReturn(Optional.of(stage));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.reorder(10L, List.of(stageRequest(100L, 1)), 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    private StageDTO stageRequest(Long id, Integer position) {
        StageDTO stageDTO = new StageDTO();
        stageDTO.setId(id);
        stageDTO.setPosition(position);
        return stageDTO;
    }

    @Test
    void findByProjectIdReturnsStages() {
        Stage first = stageWithProject(100L, 10L);
        first.setName("Todo");
        first.setPosition(1);
        Stage second = stageWithProject(101L, 10L);
        second.setName("Doing");
        second.setPosition(2);

        when(projectRepository.existsById(10L)).thenReturn(true);
        when(stageRepository.findByProjectIdOrderByPositionAscIdAsc(10L))
                .thenReturn(List.of(first, second));

        var result = service.findByProjectId(10L);

        assertEquals(2, result.size());
        assertEquals("Todo", result.get(0).getName());
        assertEquals(10L, result.get(0).getProjectId());
    }

    @Test
    void findByProjectIdRejectsMissingProject() {
        when(projectRepository.existsById(10L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByProjectId(10L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateModifiesStage() {
        Stage stage = stageWithProject(100L, 10L);
        stage.setName("Todo");
        stage.setPosition(1);

        StageDTO request = new StageDTO();
        request.setName("Done");
        request.setPosition(2);

        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN);
        when(stageRepository.findById(100L)).thenReturn(Optional.of(stage));
        when(stageRepository.save(stage)).thenReturn(stage);

        var result = service.update(100L, request, 1L);

        assertEquals("Done", result.getName());
        assertEquals(2, result.getPosition());
        verify(stageRepository).save(stage);
    }

    @Test
    void deleteRemovesStage() {
        Stage stage = stageWithProject(100L, 10L);

        when(stageRepository.findById(100L)).thenReturn(Optional.of(stage));
        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN);

        service.delete(100L, 1L);

        verify(stageRepository).delete(stage);
    }

    private Stage stageWithProject(Long stageId, Long projectId) {
        Stage stage = new Stage();
        stage.setId(stageId);
        stage.setProject(projectWithId(projectId));
        return stage;
    }

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
