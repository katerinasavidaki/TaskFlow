package com.taskflow.backend.service;

import com.taskflow.backend.core.enums.TaskStatusType;
import com.taskflow.backend.dto.TaskInsertDTO;
import com.taskflow.backend.dto.TaskReadOnlyDTO;
import com.taskflow.backend.dto.TaskUpdateDTO;

import java.util.List;

public interface ITaskService {

    TaskReadOnlyDTO createTask(TaskInsertDTO insertDTO, String creatorUsername);
    TaskReadOnlyDTO updateTask(Long id, TaskUpdateDTO updateDTO, String updaterUsername);
    TaskReadOnlyDTO getTaskById(Long id, String requesterUsername);
    TaskReadOnlyDTO assignTaskToUser(Long taskId, Long userId, String requesterUsername);
    TaskReadOnlyDTO markTaskAsCompleted(Long id, String requesterUsername);
    List<TaskReadOnlyDTO> getTasksByAssignee(Long userId, String requesterUsername);
    List<TaskReadOnlyDTO> getTasksByCreatedBy(String username, String requesterUsername);
    List<TaskReadOnlyDTO> getTasksByStatus(TaskStatusType status, String requesterUsername);
    List<TaskReadOnlyDTO> getCompletedTasks(Boolean isCompleted, String requesterUsername);
    void deleteTaskById(Long id, String requesterUsername);
    List<TaskReadOnlyDTO> getAllTasks(String requesterUsername);
}
