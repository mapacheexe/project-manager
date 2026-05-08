package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.ProjectMemberDTO;
import com.projectmanager.backend.service.ProjectMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/users")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberDTO>> findMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectMemberService.findMembers(projectId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ProjectMemberDTO> addMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long requesterId
    ) {
        return ResponseEntity.ok(projectMemberService.addMember(projectId, userId, requesterId));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ProjectMemberDTO> updateMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestBody ProjectMemberDTO request
    ) {
        return ResponseEntity.ok(projectMemberService.updateMember(projectId, userId, request, requesterId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long requesterId
    ) {
        projectMemberService.removeMember(projectId, userId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
