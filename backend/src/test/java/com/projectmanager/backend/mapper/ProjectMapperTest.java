package com.projectmanager.backend.mapper;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.model.ProjectDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMapperTest {

    private final ProjectMapper mapper = new ProjectMapper();

    @Test
    void givenProjectWithNoMembers_whenToProjectDTO_thenUserIdsEmpty() {
        Project project = new Project();
        project.setId(10L);
        project.setName("Backend");

        ProjectDTO dto = mapper.toProjectDTO(project);

        assertEquals(10L, dto.getId());
        assertEquals("Backend", dto.getName());
        assertTrue(dto.getUserIds().isEmpty());
        assertTrue(dto.getStages().isEmpty());
    }

    @Test
    void givenProjectWithMember_whenToProjectDTO_thenUserIdsPopulated() {
        User user = new User();
        user.setId(2L);

        Project project = new Project();
        project.setId(10L);

        UserProject up = new UserProject();
        up.setUser(user);
        up.setProject(project);
        project.getUserProjects().add(up);

        ProjectDTO dto = mapper.toProjectDTO(project);

        assertEquals(1, dto.getUserIds().size());
        assertEquals(2L, dto.getUserIds().get(0));
    }
}
