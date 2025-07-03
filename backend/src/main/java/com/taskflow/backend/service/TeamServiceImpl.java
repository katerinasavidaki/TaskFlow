package com.taskflow.backend.service;

import com.taskflow.backend.core.enums.RoleType;
import com.taskflow.backend.core.exceptions.*;
import com.taskflow.backend.dto.TeamInsertDTO;
import com.taskflow.backend.dto.TeamReadOnlyDTO;
import com.taskflow.backend.dto.TeamUpdateDTO;
import com.taskflow.backend.mapper.Mapper;
import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.TeamRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements ITeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TeamReadOnlyDTO createTeam(TeamInsertDTO insertDTO, String creatorUsername) {

        if (teamRepository.existsByName(insertDTO.getName())) {
            throw new AppObjectAlreadyExistsException("TEAM ", "Team with name " + insertDTO.getName() + " already exists");
        }

        User manager = userRepository.findById(insertDTO.getManagerId())
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Manager with id " + insertDTO.getManagerId()
                + " not found"));

        Set<User> members = new HashSet<>();
        if (insertDTO.getMemberIds() != null && !insertDTO.getMemberIds().isEmpty()) {
            List<User> users = userRepository.findAllById(insertDTO.getMemberIds());

            if (users.size() != insertDTO.getMemberIds().size()) {
                throw new AppObjectInvalidArgumentException("USER ", "One or more user IDs in members not found");
            }
            members.addAll(users);
        }


        Team team = Mapper.mapToTeamEntity(insertDTO, manager, members);
        teamRepository.save(team);

        return Mapper.mapToTeamReadOnlyDTO(team);
    }

    @Override
    @Transactional
    public TeamReadOnlyDTO updateTeam(Long id, TeamUpdateDTO updateDTO, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);
        checkAdminOrManager(currentUser);

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + id + " not found"));

        // Check if the name of team has changed and there is already another team with the same name
        if (!team.getName().equals(updateDTO.getName()) && teamRepository.existsByName(updateDTO.getName())) {
            throw new AppObjectAlreadyExistsException("TEAM ", "Team with name " + updateDTO.getName() + " already exists");
        }

        //Fetch manager
        User manager = userRepository.findById(updateDTO.getManagerId())
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + updateDTO.getManagerId()
                + " not found"));

        //Fetch members
        Set<User> members = new HashSet<>();
        if (updateDTO.getMemberIds() != null && !updateDTO.getMemberIds().isEmpty()) {
            List<User> users = userRepository.findAllById(updateDTO.getMemberIds());

            if (users.size() != updateDTO.getMemberIds().size()) {
                throw new AppObjectInvalidArgumentException("USER ", "One or more user IDs in members not found");
            }
            members.addAll(users);
        }

        Mapper.updateTeamEntity(team, updateDTO, manager, members);
        Team savedTeam = teamRepository.save(team);
        return Mapper.mapToTeamReadOnlyDTO(savedTeam);
    }

    @Override
    public void deleteTeam(Long id, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);
        checkAdminOrManager(currentUser);

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + id + " not found"));

        teamRepository.delete(team);
    }

    @Override
    public TeamReadOnlyDTO getTeamById(Long id, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + id +
                        " not found"));

        if (currentUser.getRole() == RoleType.MEMBER && !team.getMembers().contains(currentUser)) {
            throw new AppObjectNotAuthorizedException("TEAM ", "Not authorized to access this team");
        }

        return Mapper.mapToTeamReadOnlyDTO(team);
    }

    @Override
    public List<TeamReadOnlyDTO> getMyTeam(String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        if (currentUser.getRole() == RoleType.MEMBER) {
            return teamRepository.findByMembers(currentUser)
                    .map(Mapper::mapToTeamReadOnlyDTO)
                    .map(List::of)
                    .orElse(List.of());
        }

        return teamRepository.findByMembers(currentUser)
                .map(Mapper::mapToTeamReadOnlyDTO)
                .map(Collections::singletonList)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM", "Team not found for user"));
    }


    @Override
    public List<TeamReadOnlyDTO> getAllTeams(String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        if (currentUser.getRole() == RoleType.MEMBER) {
            return teamRepository.findByMembers(currentUser)
                    .map(Mapper::mapToTeamReadOnlyDTO)
                    .map(List::of)
                    .orElse(List.of());
        }

        return teamRepository.findAll()
                .stream()
                .map(Mapper::mapToTeamReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamReadOnlyDTO addMemberToTeam(Long teamId, Long userId, String requesterUsername) {

        User currentUser = getUserOrThrow(requesterUsername);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + teamId + " not found"));

        if (currentUser.getRole() == RoleType.ADMIN ||
                (currentUser.getRole() == RoleType.MANAGER && team.getManager().getId().equals(currentUser.getId()))) {
            User userToAdd = userRepository.findById(userId)
                    .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + userId + " not found"));

            if (team.getMembers().contains(userToAdd)) {
                throw new AppObjectInvalidArgumentException("TEAM ", "User is already a member");
            }

            team.getMembers().add(userToAdd);
            Team saved = teamRepository.save(team);
            return Mapper.mapToTeamReadOnlyDTO(saved);
        }

        throw new AppObjectNotAuthorizedException("TEAM ", "You are not allowed to add members to this team");

    }

    // Helper methods
    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Authenticated user not found"));
    }

    private void checkAdminOrManager(User user) {
        if (user.getRole() != RoleType.ADMIN && user.getRole() != RoleType.MANAGER) {
            throw new AppObjectNotAuthorizedException("USER ", "Only admins or managers can perform this action");
        }
    }
}
