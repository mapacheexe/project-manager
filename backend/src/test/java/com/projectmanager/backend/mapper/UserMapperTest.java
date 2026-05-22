package com.projectmanager.backend.mapper;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.model.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void givenUserWithNoProjects_whenToDTO_thenProjectIdsEmpty() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@test.com");

        UserDTO dto = mapper.toDTO(user);

        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@test.com", dto.getEmail());
        assertTrue(dto.getProjectIds().isEmpty());
    }

    @Test
    void givenUserWithProject_whenToDTO_thenProjectIdsPopulated() {
        Project project = new Project();
        project.setId(10L);

        UserProject up = new UserProject();
        up.setProject(project);

        User user = new User();
        user.setId(1L);
        user.getUserProjects().add(up);

        UserDTO dto = mapper.toDTO(user);

        assertEquals(1, dto.getProjectIds().size());
        assertEquals(10L, dto.getProjectIds().get(0));
    }
}
