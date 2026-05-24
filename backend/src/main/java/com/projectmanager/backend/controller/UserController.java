package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.CreateUserRequest;
import com.projectmanager.backend.model.LoginRequest;
import com.projectmanager.backend.model.LoginResponse;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UpdateUserRequest;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.service.JwtService;
import com.projectmanager.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    @GetMapping
    public ResponseEntity<List<UserDTO>> findUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request)
                .map(user -> {
                    String token = jwtService.generateToken(user.getId());
                    return ResponseEntity.ok(new LoginResponse(token, user));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> saveUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.save(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectDTO>> findProjectsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findProjectsByUserId(id));
    }

    @PostMapping("/{id}/projects")
    public ResponseEntity<ProjectDTO> createProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO request) {
        ProjectDTO projectDTO = userService.createProject(id, request);
        return  ResponseEntity.ok(projectDTO);
    }

}
