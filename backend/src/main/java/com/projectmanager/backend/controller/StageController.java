package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.StageDTO;
import com.projectmanager.backend.service.StageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StageController {

    private final StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping("/projects/{projectId}/stages")
    public ResponseEntity<List<StageDTO>> findByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(stageService.findByProjectId(projectId));
    }

    @PostMapping("/projects/{projectId}/stages")
    public ResponseEntity<StageDTO> create(
            @PathVariable Long projectId,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody StageDTO request
    ) {
        return ResponseEntity.ok(stageService.create(projectId, request, requesterId));
    }

    @PatchMapping("/stages/{id}")
    public ResponseEntity<StageDTO> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody StageDTO request
    ) {
        return ResponseEntity.ok(stageService.update(id, request, requesterId));
    }

    @DeleteMapping("/stages/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Id") Long requesterId) {
        stageService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/projects/{projectId}/stages/reorder")
    public ResponseEntity<List<StageDTO>> reorder(
            @PathVariable Long projectId,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody List<StageDTO> request
    ) {
        return ResponseEntity.ok(stageService.reorder(projectId, request, requesterId));
    }
}
