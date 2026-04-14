package com.projectmanager.backend.repository;

import com.projectmanager.backend.entity.Project;
import com.projectmanager.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT up.project FROM UserProject up WHERE up.user.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
}
