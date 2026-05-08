package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Stage;
import com.projectmanager.backend.entity.Task;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.TaskDTO;
import com.projectmanager.backend.repository.StageRepository;
import com.projectmanager.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final StageRepository stageRepository;
    private final ProjectPermissionService permissionService;
    private final ProjectMapper projectMapper;

    public TaskService(
            TaskRepository taskRepository,
            StageRepository stageRepository,
            ProjectPermissionService permissionService,
            ProjectMapper projectMapper
    ) {
        this.taskRepository = taskRepository;
        this.stageRepository = stageRepository;
        this.permissionService = permissionService;
        this.projectMapper = projectMapper;
    }

    public List<TaskDTO> findByStageId(Long stageId) {
        if (!stageRepository.existsById(stageId)) {
            throw new ResponseStatusException(NOT_FOUND, "Stage not found");
        }

        return taskRepository.findByStageIdOrderByPositionAscIdAsc(stageId)
                .stream()
                .map(projectMapper::toTaskDTO)
                .toList();
    }

    public TaskDTO findById(Long id) {
        return taskRepository.findById(id)
                .map(projectMapper::toTaskDTO)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
    }

    public TaskDTO create(Long stageId, TaskDTO request, Long requesterId) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Stage not found"));

        permissionService.requireProjectRole(stage.getProject().getId(), requesterId, OWNER, ADMIN, MEMBER);

        Task task = new Task();
        task.setStage(stage);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPosition(request.getPosition());

        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

    public TaskDTO update(Long id, TaskDTO request, Long requesterId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));

        permissionService.requireProjectRole(task.getStage().getProject().getId(), requesterId, OWNER, ADMIN, MEMBER);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPosition(request.getPosition());

        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

    public TaskDTO move(Long id, TaskDTO request, Long requesterId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
        Stage stage = stageRepository.findById(request.getStageId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Stage not found"));

        permissionService.requireProjectRole(task.getStage().getProject().getId(), requesterId, OWNER, ADMIN, MEMBER);
        permissionService.requireProjectRole(stage.getProject().getId(), requesterId, OWNER, ADMIN, MEMBER);

        task.setStage(stage);
        task.setPosition(request.getPosition());

        return projectMapper.toTaskDTO(taskRepository.save(task));
    }

    public void delete(Long id, Long requesterId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));

        permissionService.requireProjectRole(task.getStage().getProject().getId(), requesterId, OWNER, ADMIN, MEMBER);

        taskRepository.delete(task);
    }
}
