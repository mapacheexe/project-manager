package com.projectmanager.backend.mapper;

import com.projectmanager.backend.entity.User;
import com.projectmanager.backend.model.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
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
