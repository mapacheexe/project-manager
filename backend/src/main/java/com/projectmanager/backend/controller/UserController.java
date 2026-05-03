package com.projectmanager.backend.controller;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    // TODO Refactorizar a ResponseEntity
    // TODO Refactorizar los ResponseEntity existentes

    @GetMapping
    public List<UserDTO> findUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<UserDTO> findUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/projects")
    public List<Project> findProjectsByUserId(@PathVariable Long id) {
        return userService.findProjectsByUserId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
       return userService.updateUser(id, updatedUser);
    }

    @PostMapping("/save")
    public ResponseEntity<UserDTO> saveUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PostMapping("/{userId}/projects/{projectId}")
    public ResponseEntity<Long> saveProject(@PathVariable Long userId, @PathVariable Long projectId) {
        try {
            Long id = userService.saveProject(userId, projectId);
            return ResponseEntity.ok(id);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{userId}/projects")
    public ResponseEntity<ProjectDTO> createProject(@PathVariable Long userId, @RequestBody ProjectDTO projectDTO) {
        return userService.createProject(userId, projectDTO);
    }

}
