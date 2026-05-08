package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.mapper.UserMapper;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import com.projectmanager.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final UserProjectRepository userProjectRepository = mock(UserProjectRepository.class);
    private final UserMapper userMapper = new UserMapper();
    private final ProjectMapper projectMapper = new ProjectMapper();
    private final UserService service = new UserService(
            userRepository,
            projectRepository,
            userProjectRepository,
            userMapper,
            projectMapper
    );

    @Test
    void savePersistsUser() {
        User user = userWithId(1L);
        user.setName("Mario");
        when(userRepository.save(user)).thenReturn(user);

        UserDTO result = service.save(user);

        assertEquals(1L, result.getId());
        assertEquals("Mario", result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void createProjectCreatesProjectOwnedByUser() {
        User user = userWithId(1L);
        ProjectDTO request = new ProjectDTO();
        request.setName("Backend");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userProjectRepository.save(any(UserProject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDTO result = service.createProject(1L, request);

        assertEquals("Backend", result.getName());
        verify(userProjectRepository).save(any(UserProject.class));
    }

    @Test
    void createProjectStoresOwnerRole() {
        User user = userWithId(1L);
        ProjectDTO request = new ProjectDTO();
        request.setName("Backend");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userProjectRepository.save(any(UserProject.class))).thenAnswer(invocation -> {
            UserProject userProject = invocation.getArgument(0);
            assertEquals(OWNER, userProject.getRole());
            return userProject;
        });

        service.createProject(1L, request);
    }

    @Test
    void createProjectRejectsMissingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createProject(1L, new ProjectDTO())
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
        verify(projectRepository, never()).save(any(Project.class));
        verify(userProjectRepository, never()).save(any(UserProject.class));
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
