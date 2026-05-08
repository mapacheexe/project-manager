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

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
       return userService.updateUser(id, updatedUser);
    }

    @PostMapping
    public ResponseEntity<UserDTO> saveUser(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/{id}/projects")
    public List<Project> findProjectsByUserId(@PathVariable Long id) {
        return userService.findProjectsByUserId(id);
    }

    @PostMapping("/{id}/projects")
    public ResponseEntity<ProjectDTO> createProject(@PathVariable Long id, @RequestBody ProjectDTO request) {
        ProjectDTO projectDTO = userService.createProject(id, request);
        return  ResponseEntity.ok(projectDTO);
    }

    @PostMapping("/{id}/projects/{projectId}")
    public ResponseEntity<Long> assignProject(@PathVariable Long id, @PathVariable Long projectId) {
        Long userProjectId = userService.assignProject(id, projectId);
        return ResponseEntity.ok(userProjectId);
    }

}
