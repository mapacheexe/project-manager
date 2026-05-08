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

import java.util.Optional;

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

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
