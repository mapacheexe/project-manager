package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.CreateUserRequest;
import com.projectmanager.backend.model.LoginRequest;
import com.projectmanager.backend.model.LoginResponse;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UpdateUserRequest;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.service.JwtService;
import com.projectmanager.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final UserController controller = new UserController(userService, jwtService);
    private final Authentication authentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        when(authentication.getName()).thenReturn("1");
    }

    @Test
    void givenUsers_whenFindAll_thenReturnsOk() {
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.findAll()).thenReturn(List.of(user));

        ResponseEntity<List<UserDTO>> response = controller.findUsers();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void givenExistingUser_whenFindById_thenReturnsOk() {
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<UserDTO> response = controller.findUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void givenMissingUser_whenFindById_thenReturnsNotFound() {
        when(userService.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = controller.findUserById(99L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsOkWithToken() {
        LoginRequest request = new LoginRequest();
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.login(request)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(1L)).thenReturn("token.jwt.here");

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("token.jwt.here", response.getBody().getToken());
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenReturnsUnauthorized() {
        LoginRequest request = new LoginRequest();
        when(userService.login(request)).thenReturn(Optional.empty());

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void givenValidRequest_whenSaveUser_thenReturnsOk() {
        CreateUserRequest request = new CreateUserRequest();
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.save(request)).thenReturn(user);

        ResponseEntity<UserDTO> response = controller.saveUser(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void givenOwnId_whenUpdateUser_thenReturnsOk() {
        UpdateUserRequest request = new UpdateUserRequest();
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.updateUser(1L, request)).thenReturn(user);

        ResponseEntity<UserDTO> response = controller.updateUser(1L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void givenOtherId_whenUpdateUser_thenReturnsForbidden() {
        ResponseEntity<UserDTO> response = controller.updateUser(99L, authentication, new UpdateUserRequest());

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void givenOwnId_whenDeleteUser_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.deleteUser(1L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(userService).deleteUser(1L);
    }

    @Test
    void givenOtherId_whenDeleteUser_thenReturnsForbidden() {
        ResponseEntity<Void> response = controller.deleteUser(99L, authentication);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void givenOwnId_whenFindProjectsByUserId_thenReturnsOk() {
        ProjectDTO project = new ProjectDTO();
        project.setId(10L);
        when(userService.findProjectsByUserId(1L)).thenReturn(List.of(project));

        ResponseEntity<List<ProjectDTO>> response = controller.findProjectsByUserId(1L, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void givenOtherId_whenFindProjectsByUserId_thenReturnsForbidden() {
        ResponseEntity<List<ProjectDTO>> response = controller.findProjectsByUserId(99L, authentication);

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void givenOwnId_whenCreateProject_thenReturnsOk() {
        ProjectDTO request = new ProjectDTO();
        request.setName("Backend");
        ProjectDTO created = new ProjectDTO();
        created.setId(10L);
        created.setName("Backend");
        when(userService.createProject(1L, request)).thenReturn(created);

        ResponseEntity<ProjectDTO> response = controller.createProject(1L, authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Backend", response.getBody().getName());
    }

    @Test
    void givenOtherId_whenCreateProject_thenReturnsForbidden() {
        ResponseEntity<ProjectDTO> response = controller.createProject(99L, authentication, new ProjectDTO());

        assertEquals(403, response.getStatusCode().value());
    }
}
