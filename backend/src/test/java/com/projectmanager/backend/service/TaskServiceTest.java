package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.Stage;
import com.projectmanager.backend.entity.Task;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.TaskDTO;
import com.projectmanager.backend.repository.StageRepository;
import com.projectmanager.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class TaskServiceTest {

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final StageRepository stageRepository = mock(StageRepository.class);
    private final ProjectPermissionService permissionService = mock(ProjectPermissionService.class);
    private final ProjectMapper projectMapper = new ProjectMapper();
    private final TaskService service = new TaskService(
            taskRepository,
            stageRepository,
            permissionService,
            projectMapper
    );

    @Test
    void createAddsTaskToStage() {
        TaskDTO request = new TaskDTO();
        request.setTitle("Implement tests");
        request.setDescription("Cover services");
        request.setStatus("OPEN");
        request.setPosition(1);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(stageRepository.findById(100L)).thenReturn(Optional.of(stageWithProject(100L, 10L)));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = service.create(100L, request, 1L);

        assertEquals(100L, result.getStageId());
        assertEquals("Implement tests", result.getTitle());
        assertEquals("OPEN", result.getStatus());
        assertEquals(1, result.getPosition());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void moveChangesTaskStageAndPosition() {
        Task task = taskInStage(200L, 100L, 10L);
        Stage targetStage = stageWithProject(101L, 10L);
        TaskDTO request = new TaskDTO();
        request.setStageId(101L);
        request.setPosition(3);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(taskRepository.findById(200L)).thenReturn(Optional.of(task));
        when(stageRepository.findById(101L)).thenReturn(Optional.of(targetStage));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = service.move(200L, request, 1L);

        assertEquals(101L, result.getStageId());
        assertEquals(3, result.getPosition());
        verify(taskRepository).save(task);
    }

    @Test
    void createRejectsMissingStage() {
        when(stageRepository.findById(100L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(100L, new TaskDTO(), 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    private Task taskInStage(Long taskId, Long stageId, Long projectId) {
        Task task = new Task();
        task.setId(taskId);
        task.setStage(stageWithProject(stageId, projectId));
        return task;
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
