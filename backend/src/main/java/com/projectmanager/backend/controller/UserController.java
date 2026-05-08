package com.projectmanager.backend.controller;

import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.model.ProjectDTO;
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

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
       return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    @PostMapping
    public ResponseEntity<UserDTO> saveUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
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
