package com.taskflow.backend.service;

import com.taskflow.backend.core.enums.RoleType;
import com.taskflow.backend.core.enums.TaskStatusType;
import com.taskflow.backend.core.exceptions.AppObjectInvalidArgumentException;
import com.taskflow.backend.core.exceptions.AppObjectNotAuthorizedException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        if (creator.getRole() != RoleType.ADMIN && creator.getRole() != RoleType.MANAGER) {
            throw new AppObjectNotAuthorizedException("USER ", "Only Manager or Admin can create a task");
        }

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
    public TaskReadOnlyDTO updateTask(Long id, TaskUpdateDTO updateDTO, String updaterUsername) {

        User updater = getUserOrThrow(updaterUsername);

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

        boolean isAdmin = updater.getRole() == RoleType.ADMIN;
        boolean isManager = updater.getRole() == RoleType.MANAGER && task.getTeam() != null
                && task.getTeam().getManager().getId().equals(updater.getId());
        boolean isAssignedUser = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(user.getId());

        if (!(isAdmin || isManager || isAssignedUser)) {
            throw new AppObjectNotAuthorizedException("TASK ", "No permission to update this task");
        }

        Mapper.updateTaskEntity(task, updateDTO, assignedTo, team);
        Task savedTask =  taskRepository.save(task);
        return Mapper.mapToTaskReadOnlyDTO(savedTask);
    }

    @Override
    public TaskReadOnlyDTO getTaskById(Long id, String requesterUsername) {

        User user = getUserOrThrow(requesterUsername);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TASK ", "Task with id " + id + " not found"));

        boolean isAdmin = user.getRole() == RoleType.ADMIN;
        boolean isManager = user.getRole() == RoleType.MANAGER && task.getTeam() != null &&
                task.getTeam().getManager().getId().equals(user.getId());
        boolean isCreator = task.getCreatedBy() != null && task.getCreatedBy().getId().equals(user.getId());
        boolean isAssigned = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(user.getId());

        if (!(isAdmin || isManager || isCreator || isAssigned)) {
            throw new AppObjectNotAuthorizedException("TASK ", "Not authorized to view this task");
        }

        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    @Transactional
    public TaskReadOnlyDTO assignTaskToUser(Long taskId, Long userId, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppObjectNotFoundException("TASK ", "Task with id " + taskId +
                        " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + userId +
                        " not found"));

        boolean isAdmin = currentUser.getRole() == RoleType.ADMIN;
        boolean isManager = currentUser.getRole() == RoleType.MANAGER && task.getTeam() != null &&
                task.getTeam().getManager().getId().equals(currentUser.getId());

        if (!(isAdmin || isManager)) {
            throw new AppObjectNotAuthorizedException("TASK ", "Not authrorized to assign this task");
        }

        task.setAssignedTo(user);
        taskRepository.save(task);
        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    @Transactional
    public TaskReadOnlyDTO markTaskAsCompleted(Long id, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TASK ", "Task with id " + id +
                        " not found"));

        if (task.getStatus() == TaskStatusType.COMPLETED) {
            throw new AppObjectInvalidArgumentException("TASK ", "Task has already been completed");
        }

        boolean isAdmin = currentUser.getRole() == RoleType.ADMIN;
        boolean isAssignedUser = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());

        if (!(isAdmin || isAssignedUser)) {
            throw new AppObjectNotAuthorizedException("TASK ", "Only assigned user or admin can complete the task");
        }


        task.setStatus(TaskStatusType.COMPLETED);
        taskRepository.save(task);

        return Mapper.mapToTaskReadOnlyDTO(task);
    }

    @Override
    public List<TaskReadOnlyDTO> getTasksByAssignee(Long userId, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + userId +
                        " not found"));

        boolean isAdmin = currentUser.getRole() == RoleType.ADMIN;
        boolean isSelf = user.getId().equals(currentUser.getId());
        boolean isManagerOfAssignee = user.getTeam() != null &&
                user.getTeam().getManager().getId().equals(currentUser.getId());

        if (!(isAdmin || isSelf || isManagerOfAssignee)) {
            throw new AppObjectNotAuthorizedException("TASK ", "You are not authorized to view these tasks");
        }

        List<Task> tasks = taskRepository.findAllByAssignedTo(user);

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskReadOnlyDTO> getTasksByCreatedBy(String username, String requesterUsername) {

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Creator with username " + username +
                        " not found"));

        User currentUser = getUserOrThrow(requesterUsername);

        boolean isAdmin = currentUser.getRole() == RoleType.ADMIN;
        boolean isSelf = creator.getId().equals(currentUser.getId());
        boolean isManagerOfCreator = creator.getTeam() != null &&
                creator.getTeam().getManager().getId().equals(currentUser.getId());

        if (!(isAdmin || isSelf || isManagerOfCreator)) {
            throw new AppObjectNotAuthorizedException("TASK", "You are not authorized to view these tasks");
        }

        List<Task> tasks = taskRepository.findAllByCreatedBy(creator);

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskReadOnlyDTO> getTasksByStatus(TaskStatusType status, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        List<Task> tasks = new ArrayList<>();

        if (currentUser.getRole() == RoleType.ADMIN || currentUser.getRole() == RoleType.MANAGER) {
            tasks = taskRepository.findAllByStatus(status);
        } else {
            tasks = taskRepository.findAllByAssignedToAndStatus(currentUser, status);
        }

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskReadOnlyDTO> getCompletedTasks(Boolean isCompleted, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        List<Task> tasks = new ArrayList<>();

        if (currentUser.getRole() == RoleType.ADMIN || currentUser.getRole() == RoleType.MANAGER) {
            tasks = taskRepository.findAllByIsCompleted(isCompleted);
        } else {
            // MEMBER: only their own completed/incomplete tasks
            tasks = taskRepository.findAllByAssignedToAndIsCompleted(currentUser, isCompleted);
        }

        return tasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id, String requesterUsername) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("TASK ", "Task with id " + id + " not found"));

        User user = getUserOrThrow(requesterUsername);

        // Only ADMIN && MANAGER can delete a task
        if (!(user.getRole().equals(RoleType.ADMIN) || user.getRole().equals(RoleType.MANAGER))) {
            throw new AppObjectNotAuthorizedException("TASK ", "You are not authorized to delete tasks");
        }

        taskRepository.delete(task);
    }

    @Override
    public List<TaskReadOnlyDTO> getAllTasks(String requesterUsername) {

        User user = getUserOrThrow(requesterUsername);

        // if the user is admin then can have the whole view
        if (user.getRole() == RoleType.ADMIN) {
            return taskRepository.findAll()
                    .stream()
                    .map(Mapper::mapToTaskReadOnlyDTO)
                    .collect(Collectors.toList());
        }

        // if manager then can see team's tasks
        if (user.getRole() == RoleType.MANAGER && user.getTeam() != null) {
            return taskRepository.findAllByTeam(user.getTeam())
                    .stream()
                    .map(Mapper::mapToTaskReadOnlyDTO)
                    .collect(Collectors.toList());
        }

        // if user is a member then can see the assigned tasks
        List<Task> assignedTasks = taskRepository.findAllByAssignedTo(user);

        Set<Task> uniqueTasks = new HashSet<>();
        uniqueTasks.addAll(assignedTasks);

        return uniqueTasks.stream()
                .map(Mapper::mapToTaskReadOnlyDTO)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Authenticated user not found"));
    }

}
