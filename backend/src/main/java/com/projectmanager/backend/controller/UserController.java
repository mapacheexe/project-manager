package com.projectmanager.backend.controller;

import com.projectmanager.backend.model.CreateUserRequest;
import com.projectmanager.backend.model.LoginRequest;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UpdateUserRequest;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request) {
        return userService.login(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> saveUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
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
    public ResponseEntity<ProjectDTO> createProject(@PathVariable Long id, @RequestBody ProjectDTO request) {
        ProjectDTO projectDTO = userService.createProject(id, request);
        return  ResponseEntity.ok(projectDTO);
    }

}
