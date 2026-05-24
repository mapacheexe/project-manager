package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.ProjectMemberDTO;
import com.projectmanager.backend.service.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectMemberService.addMember(projectId, userId, requesterId(authentication)));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ProjectMemberDTO> updateMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication,
            @Valid @RequestBody ProjectMemberDTO request
    ) {
        return ResponseEntity.ok(projectMemberService.updateMember(projectId, userId, request, requesterId(authentication)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        projectMemberService.removeMember(projectId, userId, requesterId(authentication));
        return ResponseEntity.noContent().build();
    }

    private Long requesterId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
