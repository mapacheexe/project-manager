package com.projectmanager.backend.repository;

import com.projectmanager.backend.entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);

    Optional<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);

    List<UserProject> findByProjectId(Long projectId);
}
