package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.CreateUserRequest;
import com.projectmanager.backend.model.LoginRequest;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UpdateUserRequest;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final UserController controller = new UserController(userService);

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
    void givenValidCredentials_whenLogin_thenReturnsOk() {
        LoginRequest request = new LoginRequest();
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.login(request)).thenReturn(Optional.of(user));

        var response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenReturnsUnauthorized() {
        LoginRequest request = new LoginRequest();
        when(userService.login(request)).thenReturn(Optional.empty());

        var response = controller.login(request);

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
    void givenValidRequest_whenUpdateUser_thenReturnsOk() {
        UpdateUserRequest request = new UpdateUserRequest();
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(userService.updateUser(1L, request)).thenReturn(user);

        ResponseEntity<UserDTO> response = controller.updateUser(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void givenExistingUser_whenDeleteUser_thenReturnsNoContent() {
        ResponseEntity<Void> response = controller.deleteUser(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(userService).deleteUser(1L);
    }

    @Test
    void givenExistingUser_whenFindProjectsByUserId_thenReturnsOk() {
        ProjectDTO project = new ProjectDTO();
        project.setId(10L);
        when(userService.findProjectsByUserId(1L)).thenReturn(List.of(project));

        ResponseEntity<List<ProjectDTO>> response = controller.findProjectsByUserId(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void givenValidRequest_whenCreateProject_thenReturnsOk() {
        ProjectDTO request = new ProjectDTO();
        request.setName("Backend");
        ProjectDTO created = new ProjectDTO();
        created.setId(10L);
        created.setName("Backend");
        when(userService.createProject(1L, request)).thenReturn(created);

        ResponseEntity<ProjectDTO> response = controller.createProject(1L, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Backend", response.getBody().getName());
    }
}
