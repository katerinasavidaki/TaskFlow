package com.taskflow.backend.repository;

import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {

    Optional<Team> findByName(String name);
    Optional<Team> findByManager(User manager);
}
