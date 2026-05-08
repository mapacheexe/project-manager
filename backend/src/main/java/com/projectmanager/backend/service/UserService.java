package com.projectmanager.backend.service;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.mapper.ProjectMapper;
import com.projectmanager.backend.mapper.UserMapper;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.UserDTO;
import com.projectmanager.backend.repository.ProjectRepository;
import com.projectmanager.backend.repository.UserProjectRepository;
import com.projectmanager.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.projectmanager.backend.model.ProjectRole.OWNER;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    public UserService(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            UserProjectRepository userProjectRepository,
            UserMapper userMapper,
            ProjectMapper projectMapper
    ) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.userProjectRepository = userProjectRepository;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
    }

    public List<UserDTO> findAll() {
        return  userRepository.findAll().stream().map(userMapper::toDTO).toList();
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    public UserDTO save(User user) {
        return userMapper.toDTO(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, User request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        return userMapper.toDTO(userRepository.save(user));

    }

    public List<ProjectDTO> findProjectsByUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }

        return userRepository.findProjectsByUserId(id)
                .stream()
                .map(projectMapper::toProjectDTO)
                .toList();
    }

    public ProjectDTO createProject(Long userId, ProjectDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        Project project = new Project();
        project.setName(request.getName());

        projectRepository.save(project);

        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setRole(OWNER);
        userProject.setJoinedAt(LocalDate.now());

        userProjectRepository.save(userProject);

        return projectMapper.toProjectDTO(project);
    }

}
