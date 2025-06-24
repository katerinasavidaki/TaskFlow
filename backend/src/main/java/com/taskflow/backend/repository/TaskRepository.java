package com.taskflow.backend.repository;

import com.taskflow.backend.core.enums.TaskStatusType;
import com.taskflow.backend.model.Task;
import com.taskflow.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByTitle(String title);
    List<Task> findByAssignedTo(User AssignedTo);
    List<Task> findByCreatedBy(User createdBy);
    List<Task> findByIsCompleted(Boolean isCompleted);
    List<Task> findByStatus(TaskStatusType status);

}
