package com.taskflow.backend.service;

import com.taskflow.backend.core.enums.TaskStatusType;
import com.taskflow.backend.core.exceptions.AppObjectInvalidArgumentException;
import com.taskflow.backend.core.exceptions.AppObjectNotFoundException;
import com.taskflow.backend.dto.TaskInsertDTO;
import com.taskflow.backend.dto.TaskReadOnlyDTO;
import com.taskflow.backend.dto.TaskUpdateDTO;
import com.taskflow.backend.mapper.Mapper;
import com.taskflow.backend.model.Task;
import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.TaskRepository;
import com.taskflow.backend.repository.TeamRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements ITaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskReadOnlyDTO createTask(TaskInsertDTO insertDTO, String creatorUsername) {

        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Creator with username " +
                        creatorUsername + " not found"));

        User assignedTo = null;
        if (insertDTO.getAssignedToId() != null) {
            assignedTo = userRepository.findById(insertDTO.getAssignedToId())
                    .orElseThrow(() -> new AppObjectNotFoundException("USER", "Assigned user with id " + insertDTO.getAssignedToId()
                            + " not found"));
        }

        Team team = null;
        if (insertDTO.getTeamId() != null) {
            team = teamRepository.findById(insertDTO.getTeamId()).orElseThrow(
                    () -> new AppObjectNotFoundException("TEAM", "Team with id " + insertDTO.getTeamId() +
                            " not found"));
        }

        Task task = Mapper.mapToTaskEntity(insertDTO, assignedTo, team);

        task.setCreatedBy(creator);
        task.setStatus(TaskStatusType.TODO);
        taskRepository.save(task);
        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    @Transactional
    public TaskReadOnlyDTO updateTask(Long id, TaskUpdateDTO updateDTO) {

        Task task = taskRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("TASK ", "Task with id " + id + " not found"));

        User assignedTo = null;
        if (updateDTO.getAssignedToId() != null) {
            assignedTo = userRepository.findById(updateDTO.getAssignedToId())
                    .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + updateDTO.getAssignedToId()
                    + " not found"));
        }

        Team team = null;
        if (updateDTO.getTeamId() != null) {
            team = teamRepository.findById(updateDTO.getTeamId())
                    .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + updateDTO.getTeamId()
                            + " not found"));
        }

        Mapper.updateTaskEntity(task, updateDTO, assignedTo, team);
        Task savedTask =  taskRepository.save(task);
        return Mapper.mapToTaskReadOnlyDTO(savedTask);
    }

    @Override
    public TaskReadOnlyDTO getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TASK ", "Task with id " + id + " not found"));

        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    @Transactional
    public TaskReadOnlyDTO assignTaskToUser(Long taskId, Long userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppObjectNotFoundException("TASK ", "Task with id " + taskId +
                        " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + userId +
                        " not found"));

        task.setAssignedTo(user);
        taskRepository.save(task);
        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    @Transactional
    public TaskReadOnlyDTO markTaskAsCompleted(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TASK ", "Task with id " + id +
                        " not found"));

        if (task.getStatus() == TaskStatusType.COMPLETED) {
            throw new AppObjectInvalidArgumentException("TASK ", "Task has already been completed");
        }

        task.setStatus(TaskStatusType.COMPLETED);
        taskRepository.save(task);

        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    public List<TaskReadOnlyDTO> getTasksByAssignee(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + userId +
                        " not found"));

        List<Task> tasks = taskRepository.findAllByAssignedTo(user);

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .toList();
    }

    @Override
    public List<TaskReadOnlyDTO> getTasksByCreatedBy(String username) {

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Creator with username " + username +
                        " not found"));

        List<Task> tasks = taskRepository.findAllByCreatedBy(creator);

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskReadOnlyDTO> getTasksByStatus(TaskStatusType status) {

        List<Task> tasks = taskRepository.findAllByStatus(status);
        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskReadOnlyDTO> getCompletedTasks(Boolean isCompleted) {

        List<Task> tasks = taskRepository.findAllByIsCompleted(isCompleted);

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("TASK ", "Task with id " + id + " not found"));

        taskRepository.delete(task);
    }

    @Override
    public List<TaskReadOnlyDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }
}
