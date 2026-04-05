package com.projectmanager.backend.controller;

import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.repository.UserRepository;
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

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User response = userService.save(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.getUserById(id)
                .map(
                        user -> {
                            user.setName(updatedUser.getName());
                            user.setEmail(updatedUser.getEmail());
                            user.setPassword((updatedUser.getPassword()));
                            User saved = userService.save(user);
                            return ResponseEntity.ok(saved);
                        }
                ).orElseThrow( () -> new RuntimeException("User not found"));
    }
}
