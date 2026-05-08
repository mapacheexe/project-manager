package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.TaskDTO;
import com.projectmanager.backend.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/stages/{stageId}/tasks")
    public ResponseEntity<List<TaskDTO>> findByStageId(@PathVariable Long stageId) {
        return ResponseEntity.ok(taskService.findByStageId(stageId));
    }

    @PostMapping("/stages/{stageId}/tasks")
    public ResponseEntity<TaskDTO> create(
            @PathVariable Long stageId,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody TaskDTO request
    ) {
        return ResponseEntity.ok(taskService.create(stageId, request, requesterId));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody TaskDTO request
    ) {
        return ResponseEntity.ok(taskService.update(id, request, requesterId));
    }

    @PatchMapping("/tasks/{id}/move")
    public ResponseEntity<TaskDTO> move(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody TaskDTO request
    ) {
        return ResponseEntity.ok(taskService.move(id, request, requesterId));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Id") Long requesterId) {
        taskService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }
}
