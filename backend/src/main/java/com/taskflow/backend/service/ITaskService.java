package com.taskflow.backend.service;

import com.taskflow.backend.core.enums.TaskStatusType;
import com.taskflow.backend.dto.TaskInsertDTO;
import com.taskflow.backend.dto.TaskReadOnlyDTO;
import com.taskflow.backend.dto.TaskUpdateDTO;

import java.util.List;

public interface ITaskService {

    TaskReadOnlyDTO createTask(TaskInsertDTO insertDTO, String creatorUsername);
    TaskReadOnlyDTO updateTask(Long id, TaskUpdateDTO updateDTO);
    TaskReadOnlyDTO getTaskById(Long id);
    TaskReadOnlyDTO assignTaskToUser(Long taskId, Long userId);
    TaskReadOnlyDTO markTaskAsCompleted(Long id);
    List<TaskReadOnlyDTO> getTasksByAssignee(Long userId);
    List<TaskReadOnlyDTO> getTasksByCreatedBy(String username);
    List<TaskReadOnlyDTO> getTasksByStatus(TaskStatusType status);
    List<TaskReadOnlyDTO> getCompletedTasks(Boolean isCompleted);
    void deleteTaskById(Long id);
    List<TaskReadOnlyDTO> getAllTasks();
}
