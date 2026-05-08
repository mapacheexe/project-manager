package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.model.ProjectMemberDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import com.projectmanager.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.ADMIN;
import static com.projectmanager.backend.model.ProjectRole.MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class ProjectMemberServiceTest {

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
    private final ProjectPermissionService permissionService = mock(ProjectPermissionService.class);
    private final ProjectMapper projectMapper = new ProjectMapper();
    private final ProjectMemberService service = new ProjectMemberService(
            projectRepository,
            userRepository,
            userProjectRepository,
            permissionService,
            projectMapper
    );

    @Test
    void addMemberCreatesMemberRelationship() {
        User user = userWithId(2L);
        Project project = projectWithId(10L);

        doNothing().when(permissionService).requireProjectRole(any(), any(), any());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userProjectRepository.existsByUserIdAndProjectId(2L, 10L)).thenReturn(false);
        when(userProjectRepository.save(any(UserProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectMemberDTO result = service.addMember(10L, 2L, 1L);

        assertEquals(2L, result.getUserId());
        assertEquals(10L, result.getProjectId());
        assertEquals(MEMBER, result.getRole());
        assertNotNull(result.getJoinedAt());
        verify(userProjectRepository).save(any(UserProject.class));
    }

    @Test
    void addMemberRejectsDuplicateRelationship() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithId(2L)));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(projectWithId(10L)));
        when(userProjectRepository.existsByUserIdAndProjectId(2L, 10L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.addMember(10L, 2L, 1L)
        );

        assertEquals(CONFLICT, exception.getStatusCode());
        verify(userProjectRepository, never()).save(any(UserProject.class));
    }

    @Test
    void addMemberRejectsMissingUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.addMember(10L, 2L, 1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        verify(projectRepository, never()).findById(10L);
        verify(userProjectRepository, never()).save(any(UserProject.class));
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @Test
    void findMembersReturnsProjectMembers() {
        UserProject member = new UserProject();
        member.setId(1L);
        member.setUser(userWithId(2L));
        member.setProject(projectWithId(10L));
        member.setRole(MEMBER);

        when(projectRepository.existsById(10L)).thenReturn(true);
        when(userProjectRepository.findByProjectId(10L)).thenReturn(List.of(member));

        var result = service.findMembers(10L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUserId());
    }

    @Test
    void findMembersRejectsMissingProject() {
        when(projectRepository.existsById(10L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findMembers(10L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateMemberChangesRole() {
        UserProject existing = new UserProject();
        existing.setUser(userWithId(2L));
        existing.setProject(projectWithId(10L));
        existing.setRole(MEMBER);

        ProjectMemberDTO request = new ProjectMemberDTO();
        request.setRole(ADMIN);

        when(userProjectRepository.findByUserIdAndProjectId(2L, 10L))
                .thenReturn(Optional.of(existing));
        when(userProjectRepository.save(existing)).thenReturn(existing);

        var result = service.updateMember(10L, 2L, request, 1L);

        assertEquals(ADMIN, result.getRole());
        verify(userProjectRepository).save(existing);
    }

    @Test
    void removeMemberDeletesRelationship() {
        UserProject existing = new UserProject();
        existing.setUser(userWithId(2L));
        existing.setProject(projectWithId(10L));

        when(userProjectRepository.findByUserIdAndProjectId(2L, 10L))
                .thenReturn(Optional.of(existing));

        service.removeMember(10L, 2L, 1L);

        verify(userProjectRepository).delete(existing);
    }

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
