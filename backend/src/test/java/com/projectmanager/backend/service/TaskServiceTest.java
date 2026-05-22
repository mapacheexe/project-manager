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

import java.util.List;
import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
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
    void givenStageAndPermission_whenCreate_thenTaskAdded() {
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
    void givenMissingStage_whenCreate_thenNotFoundThrown() {
        when(stageRepository.findById(100L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(100L, new TaskDTO(), 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenExistingStage_whenFindByStageId_thenReturnsTasks() {
        Task first = taskInStage(200L, 100L, 10L);
        first.setTitle("Test 1");
        first.setPosition(1);
        Task second = taskInStage(201L, 100L, 10L);
        second.setTitle("Test 2");
        second.setPosition(2);

        when(stageRepository.existsById(100L)).thenReturn(true);
        when(taskRepository.findByStageIdOrderByPositionAscIdAsc(100L))
                .thenReturn(List.of(first, second));

        var result = service.findByStageId(100L);

        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getStageId());
        assertEquals("Test 1", result.get(0).getTitle());
    }

    @Test
    void givenMissingStage_whenFindByStageId_thenNotFoundThrown() {
        when(stageRepository.existsById(100L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByStageId(100L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenExistingTask_whenFindById_thenReturnsTask() {
        Task task = taskInStage(200L, 100L, 10L);
        when(taskRepository.findById(200L)).thenReturn(Optional.of(task));

        var result = service.findById(200L);

        assertEquals(200L, result.getId());
    }

    @Test
    void givenMissingTask_whenFindById_thenNotFoundThrown() {
        when(taskRepository.findById(200L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(200L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenExistingTaskAndPermission_whenUpdate_thenModified() {
        Task task = taskInStage(200L, 100L, 10L);
        task.setTitle("Old title");
        task.setPosition(1);

        TaskDTO request = new TaskDTO();
        request.setTitle("New title");
        request.setDescription("Updated");
        request.setStatus("DONE");
        request.setPosition(2);

        when(taskRepository.findById(200L)).thenReturn(Optional.of(task));
        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN, MEMBER);
        when(taskRepository.save(task)).thenReturn(task);

        var result = service.update(200L, request, 1L);

        assertEquals("New title", result.getTitle());
        assertEquals(2, result.getPosition());
        verify(taskRepository).save(task);
    }

    @Test
    void givenExistingTaskAndStageInSameProject_whenMove_thenStageAndPositionUpdated() {
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
    void givenMissingTask_whenMove_thenNotFoundThrown() {
        when(taskRepository.findById(200L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.move(200L, new TaskDTO(), 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenMissingTargetStage_whenMove_thenNotFoundThrown() {
        Task task = taskInStage(200L, 100L, 10L);
        TaskDTO request = new TaskDTO();
        request.setStageId(101L);

        when(taskRepository.findById(200L)).thenReturn(Optional.of(task));
        when(stageRepository.findById(101L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.move(200L, request, 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenTasksInDifferentProjects_whenMove_thenChecksBothPermissions() {
        Task task = taskInStage(200L, 100L, 10L);
        Stage targetStage = stageWithProject(101L, 20L);
        TaskDTO request = new TaskDTO();
        request.setStageId(101L);
        request.setPosition(1);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(taskRepository.findById(200L)).thenReturn(Optional.of(task));
        when(stageRepository.findById(101L)).thenReturn(Optional.of(targetStage));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.move(200L, request, 1L);

        verify(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN, MEMBER);
        verify(permissionService).requireProjectRole(20L, 1L, OWNER, ADMIN, MEMBER);
    }

    @Test
    void givenMissingTask_whenUpdate_thenNotFoundThrown() {
        when(taskRepository.findById(200L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.update(200L, new TaskDTO(), 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenMissingTask_whenDelete_thenNotFoundThrown() {
        when(taskRepository.findById(200L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.delete(200L, 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenExistingTaskAndPermission_whenDelete_thenDeleted() {
        Task task = taskInStage(200L, 100L, 10L);

        when(taskRepository.findById(200L)).thenReturn(Optional.of(task));
        doNothing().when(permissionService).requireProjectRole(10L, 1L, OWNER, ADMIN, MEMBER);

        service.delete(200L, 1L);

        verify(taskRepository).delete(task);
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
