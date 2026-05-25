package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.ProjectMemberDTO;
import com.projectmanager.backend.service.ProjectMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectMemberControllerTest {

    private final ProjectMemberService projectMemberService = mock(ProjectMemberService.class);
    private final ProjectMemberController controller = new ProjectMemberController(projectMemberService);
    private final Authentication authentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void givenExistingProject_whenFindMembers_thenReturnsOk() {
        ProjectMemberDTO member = new ProjectMemberDTO();
        member.setUserId(2L);
        member.setRole(MEMBER);
        when(projectMemberService.findMembers(10L, 1L)).thenReturn(List.of(member));

        ResponseEntity<List<ProjectMemberDTO>> response = controller.findMembers(10L, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(2L, response.getBody().get(0).getUserId());
    }

    @Test
    void givenValidRequest_whenAddMember_thenReturnsOk() {
        ProjectMemberDTO created = new ProjectMemberDTO();
        created.setUserId(2L);
        created.setProjectId(10L);
        created.setRole(MEMBER);
        when(projectMemberService.addMember(10L, 2L, 1L)).thenReturn(created);

        ResponseEntity<ProjectMemberDTO> response = controller.addMember(10L, 2L, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(MEMBER, response.getBody().getRole());
    }

    @Test
    void givenValidRequest_whenUpdateMember_thenReturnsOk() {
        ProjectMemberDTO request = new ProjectMemberDTO();
        request.setRole(ADMIN);
        ProjectMemberDTO updated = new ProjectMemberDTO();
        updated.setUserId(2L);
        updated.setRole(ADMIN);
        when(projectMemberService.updateMember(10L, 2L, request, 1L)).thenReturn(updated);

        ResponseEntity<ProjectMemberDTO> response = controller.updateMember(10L, 2L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ADMIN, response.getBody().getRole());
    }

    @Test
    void givenExistingMembership_whenRemoveMember_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.removeMember(10L, 2L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(projectMemberService).removeMember(10L, 2L, 1L);
    }

    @Test
    void givenValidRequest_whenLeaveProject_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.leaveProject(10L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(projectMemberService).leaveProject(10L, 1L);
    }
}
