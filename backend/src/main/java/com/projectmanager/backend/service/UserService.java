package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import com.projectmanager.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserProjectRepository userProjectRepository;

    public List<User> findAll() {
        return  userRepository.findAll();
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO);
    }

    public ResponseEntity<UserDTO> save(User user) {
        try {
            userRepository.save(user);
            UserDTO userDTO = toDTO(user);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    public ResponseEntity<UserDTO> updateUser(Long id, User request) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setName(request.getName());
            user.setEmail(request.getEmail());

            UserDTO dto = toDTO(userRepository.save(user));

            return ResponseEntity.ok(dto);
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

    public List<Project> findProjectsByUserId(Long id) {
        return userRepository.findProjectsByUserId(id);
    }

    public ResponseEntity<Long> saveProject(Long userId, Long projectId) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            UserProject userProject = new UserProject();
            userProject.setUser(user);
            userProject.setProject(project);

            userProjectRepository.save(userProject);

            return ResponseEntity.ok(userProject.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private UserDTO toDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());

        dto.setProjectIds(
                user.getUserProjects()
                        .stream()
                        .map(up -> up.getProject().getId())
                        .toList()
        );

        return dto;
    }

}
