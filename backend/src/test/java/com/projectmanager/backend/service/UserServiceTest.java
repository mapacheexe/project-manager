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

import java.util.List;
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
    void findAllReturnsAllUsers() {
        User first = userWithId(1L);
        first.setName("Alice");
        User second = userWithId(2L);
        second.setName("Bob");

        when(userRepository.findAll()).thenReturn(List.of(first, second));

        var result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void findByIdReturnsUserIfExists() {
        User user = userWithId(1L);
        user.setName("Alice");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = service.findById(1L);

        assertEquals(1L, result.get().getId());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    void updateUserModifiesExistingUser() {
        User existing = userWithId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@example.com");

        User request = new User();
        request.setName("New Name");
        request.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        var result = service.updateUser(1L, request);

        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        verify(userRepository).save(existing);
    }

    @Test
    void findProjectsByUserIdReturnsMappedProjects() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findProjectsByUserId(1L)).thenReturn(List.of(
                projectWithId(10L),
                projectWithId(20L)
        ));

        var result = service.findProjectsByUserId(1L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(20L, result.get(1).getId());
    }

    @Test
    void findProjectsByUserIdRejectsMissingUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findProjectsByUserId(1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
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
