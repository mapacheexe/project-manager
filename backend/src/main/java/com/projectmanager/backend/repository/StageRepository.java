package com.projectmanager.backend.repository;

import com.projectmanager.backend.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StageRepository extends JpaRepository<Stage, Long> {
    List<Stage> findByProjectIdOrderByPositionAscIdAsc(Long projectId);
}
