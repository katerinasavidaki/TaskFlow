package com.taskflow.backend.service;

import com.taskflow.backend.dto.TaskInsertDTO;
import com.taskflow.backend.dto.TaskReadOnlyDTO;
import com.taskflow.backend.dto.TaskUpdateDTO;

import java.util.List;

public interface ITaskService {

    TaskReadOnlyDTO createTask(TaskInsertDTO insertDTO);
    TaskReadOnlyDTO updateTask(Long id, TaskUpdateDTO updateDTO);
    TaskReadOnlyDTO getTaskById(Long id);
    void deleteTaskById(Long id);
    List<TaskReadOnlyDTO> getAllTasks();
}
