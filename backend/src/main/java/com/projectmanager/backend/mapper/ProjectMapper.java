package com.projectmanager.backend.mapper;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.Stage;
import com.projectmanager.backend.entity.Task;
import com.projectmanager.backend.entity.UserProject;
import com.projectmanager.backend.model.ProjectDTO;
import com.projectmanager.backend.model.ProjectMemberDTO;
import com.projectmanager.backend.model.StageDTO;
import com.projectmanager.backend.model.TaskDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class ProjectMapper {

    public ProjectDTO toProjectDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();

        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setUserIds(
                project.getUserProjects()
                        .stream()
                        .map(up -> up.getUser().getId())
                        .toList()
        );
        dto.setStages(
                project.getStages()
                        .stream()
                        .sorted(Comparator.comparing(Stage::getPosition, Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(this::toStageDTO)
                        .toList()
        );

        return dto;
    }

    public StageDTO toStageDTO(Stage stage) {
        StageDTO dto = new StageDTO();

        dto.setId(stage.getId());
        dto.setName(stage.getName());
        dto.setProjectId(stage.getProject().getId());
        dto.setPosition(stage.getPosition());
        dto.setTasks(
                stage.getTasks()
                        .stream()
                        .sorted(Comparator.comparing(Task::getPosition, Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(this::toTaskDTO)
                        .toList()
        );

        return dto;
    }

    public TaskDTO toTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();

        dto.setId(task.getId());
        dto.setStageId(task.getStage().getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPosition(task.getPosition());

        return dto;
    }

    public ProjectMemberDTO toProjectMemberDTO(UserProject userProject) {
        ProjectMemberDTO dto = new ProjectMemberDTO();

        dto.setId(userProject.getId());
        dto.setUserId(userProject.getUser().getId());
        dto.setProjectId(userProject.getProject().getId());
        dto.setRole(userProject.getRole());
        dto.setJoinedAt(userProject.getJoinedAt());

        return dto;
    }
}
