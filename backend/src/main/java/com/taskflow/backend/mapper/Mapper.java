package com.taskflow.backend.mapper;

import com.taskflow.backend.dto.*;
import com.taskflow.backend.model.Task;
import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

public class Mapper {

    private Mapper(){}

    public static User mapToUserEntity(UserInsertDTO insertDTO, Team team) {

        return User.builder().firstname(insertDTO.getFirstname())
                .lastname(insertDTO.getLastname())
                .phoneNumber(insertDTO.getPhoneNumber())
                .afm(insertDTO.getAfm())
                .username(insertDTO.getUsername())
                .password(insertDTO.getPassword())      // raw here, encoding at service
                .role(insertDTO.getRole())
                .isActive(insertDTO.getIsActive())
                .team(team)
                .build();
    }

    public static void updateUserEntity(User user, UserUpdateDTO userUpdateDTO, Team team) {
        if (userUpdateDTO.getFirstname() != null) user.setFirstname(userUpdateDTO.getFirstname());
        if (userUpdateDTO.getLastname() != null) user.setLastname(userUpdateDTO.getLastname());
        if (userUpdateDTO.getPhoneNumber() != null) user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        if (userUpdateDTO.getAfm() != null) user.setAfm(userUpdateDTO.getAfm());
        if (userUpdateDTO.getRole() != null) user.setRole(userUpdateDTO.getRole());
        if (userUpdateDTO.getIsActive() != null) user.setIsActive(userUpdateDTO.getIsActive());
        if (team != null) user.setTeam(team);
    }

    public static UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {

        return UserReadOnlyDTO.builder().id(user.getId())
                .uuid(user.getUuid())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .afm(user.getAfm())
                .phoneNumber(user.getPhoneNumber())
                .username(user.getUsername())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .teamName(user.getTeam().getName())
                .build();
    }

    public static Team mapToTeamEntity(TeamInsertDTO teamInsertDTO, User manager, Set<User> members) {

        Team team = new Team();
        team.setName(teamInsertDTO.getName());
        team.setManager(manager);
        for (User member : members) team.addMember(member);

        return team;
    }

    public static void updateTeamEntity(Team team, TeamUpdateDTO updateDTO, User manager, Set<User> members) {

        if (updateDTO.getName() != null && !updateDTO.getName().isBlank()) team.setName(updateDTO.getName());
        if (manager != null) team.setManager(manager);
        if (members != null && !members.isEmpty()) {
            for (User member : members) team.addMember(member);
        }
    }

    public static TeamReadOnlyDTO mapToTeamReadOnlyDTO(Team team) {

        String managerFullName = team.getManager().getFirstname() + " " + team.getManager().getLastname();

        Set<String> memberFullNames = team.getMembers()
                .stream()
                .map(m -> m.getFirstname() + " " + m.getLastname())
                .collect(Collectors.toSet());

        return new TeamReadOnlyDTO(team.getId(), team.getName(), managerFullName, memberFullNames);
    }

    public static Task mapToTaskEntity(TaskInsertDTO insertDTO, User assignedTo, Team team) {

        return Task.builder()
                .title(insertDTO.getTitle())
                .description(insertDTO.getDescription())
                .priority(insertDTO.getPriority())
                .status(insertDTO.getStatus())
                .dueDate(insertDTO.getDueDate())
                .isCompleted(insertDTO.getIsCompleted())
                .assignedTo(assignedTo)
                .team(team)
                .build();
    }

    public static void updateTaskEntity(Task task, TaskUpdateDTO updateDTO, User assignedTo, Team team) {

        if (updateDTO.getTitle() != null && !updateDTO.getTitle().isBlank()) task.setTitle(updateDTO.getTitle());
        if (updateDTO.getDescription() != null && !updateDTO.getDescription().isBlank()) task.setDescription(updateDTO.getDescription());
        if (updateDTO.getIsCompleted() != null) task.setIsCompleted(updateDTO.getIsCompleted());
        if (updateDTO.getPriority() != null) task.setPriority(updateDTO.getPriority());
        if (updateDTO.getStatus() != null) task.setStatus(updateDTO.getStatus());
        if (updateDTO.getDueDate() != null) task.setDueDate(updateDTO.getDueDate());
        if (assignedTo != null) task.setAssignedTo(assignedTo);
        if (team != null) task.setTeam(team);
    }

    public static TaskReadOnlyDTO mapToTaskReadOnlyDTO(Task task) {

        String createdByUsername = task.getCreatedBy() != null ? task.getCreatedBy().getUsername() : null;
        String assignedToUsername = task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null;
        String teamName = task.getTeam() != null ? task.getTeam().getName() : null;

        return TaskReadOnlyDTO.builder()
                .id(task.getId())
                .assignedToUsername(assignedToUsername)
                .createdByUsername(createdByUsername)
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .status(task.getStatus().name())
                .dueDate(task.getDueDate())
                .teamName(teamName)
                .isCompleted(task.getIsCompleted())
                .build();

    }
}
