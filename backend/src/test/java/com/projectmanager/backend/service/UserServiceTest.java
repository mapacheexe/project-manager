package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.mapper.UserMapper;
import com.projectmanager.backend.model.CreateUserRequest;
import com.projectmanager.backend.model.LoginRequest;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UpdateUserRequest;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import com.projectmanager.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
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
    void givenValidRequest_whenSave_thenPersistsUser() {
        User saved = userWithId(1L);
        saved.setName("Mario");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        CreateUserRequest request = new CreateUserRequest();
        request.setName("Mario");
        request.setEmail("mario@test.com");
        request.setPassword("secret");

        UserDTO result = service.save(request);

        assertEquals(1L, result.getId());
        assertEquals("Mario", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsUser() {
        User user = userWithId(1L);
        user.setPassword(new BCryptPasswordEncoder().encode("secret"));

        LoginRequest request = new LoginRequest();
        request.setEmail("mario@test.com");
        request.setPassword("secret");

        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));

        Optional<UserDTO> result = service.login(request);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void givenWrongPassword_whenLogin_thenReturnsEmpty() {
        User user = userWithId(1L);
        user.setPassword(new BCryptPasswordEncoder().encode("correct"));

        LoginRequest request = new LoginRequest();
        request.setEmail("mario@test.com");
        request.setPassword("wrong");

        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));

        Optional<UserDTO> result = service.login(request);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenTwoUsers_whenFindAll_thenReturnsBoth() {
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
    void givenExistingUser_whenFindById_thenReturnsUser() {
        User user = userWithId(1L);
        user.setName("Alice");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    void givenMissingUser_whenFindById_thenReturnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        var result = service.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenExistingUser_whenUpdateUser_thenModified() {
        User existing = userWithId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@example.com");

        UpdateUserRequest request = new UpdateUserRequest();
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
    void givenExistingUser_whenDeleteUser_thenDeleted() {
        when(userRepository.existsById(1L)).thenReturn(true);

        service.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void givenMissingUser_whenDeleteUser_thenNotFoundThrown() {
        when(userRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteUser(99L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenExistingUser_whenFindProjectsByUserId_thenReturnsMapped() {
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
    void givenMissingUser_whenFindProjectsByUserId_thenNotFoundThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findProjectsByUserId(1L)
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenExistingUser_whenCreateProject_thenProjectCreated() {
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
    void givenExistingUser_whenCreateProject_thenOwnerRoleAssigned() {
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
    void givenMissingUser_whenUpdateUser_thenNotFoundThrown() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateUser(99L, new UpdateUserRequest())
        );

        assertEquals(NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void givenMissingUser_whenCreateProject_thenNotFoundThrown() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createProject(99L, new ProjectDTO())
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
