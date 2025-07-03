package com.taskflow.backend.service;

import com.taskflow.backend.core.enums.RoleType;
import com.taskflow.backend.core.exceptions.AppObjectInvalidArgumentException;
import com.taskflow.backend.core.exceptions.AppObjectNotFoundException;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserUpdateDTO;
import com.taskflow.backend.mapper.Mapper;
import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.TeamRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public UserReadOnlyDTO updateUser(Long id, UserUpdateDTO updateDTO, String requesterUsername) {

        User currentUser = getUserByUsername(requesterUsername);

        User userToUpdate = userRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with id " + id + " not found"));

        // Role check: only ADMIN and MANAGER can update
        if (currentUser.getRole() == RoleType.MEMBER && !currentUser.getId().equals(userToUpdate.getId())) {
            throw new AppObjectInvalidArgumentException("USER ", "Members can only update their own profile");
        }
        if (currentUser.getRole() == RoleType.MANAGER &&
                (userToUpdate.getTeam() == null ||
                        userToUpdate.getTeam().getManager() == null ||
                        !userToUpdate.getTeam().getManager().getId().equals(currentUser.getId()))) {
            throw new AppObjectInvalidArgumentException("USER", "Manager can only update users of their own team");
        }

        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().isBlank()) {
            Optional<User> userWithSamePhone = userRepository.findByPhoneNumber(updateDTO.getPhoneNumber());
            if (userWithSamePhone.isPresent() && !userWithSamePhone.get().getId().equals(userToUpdate.getId())) {
                throw new AppObjectInvalidArgumentException("USER ", "Phone number already exists in another user");
            }
            userToUpdate.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        if (updateDTO.getAfm() != null && !updateDTO.getAfm().isBlank()) {
            Optional<User> userWithSameAfm = userRepository.findByAfm(updateDTO.getAfm());
            if (userWithSameAfm.isPresent() && !userWithSameAfm.get().getId().equals(userToUpdate.getId())) {
                throw new AppObjectInvalidArgumentException("USER ", "Afm already exists in another user");
            }
            userToUpdate.setAfm(updateDTO.getAfm());
        }

        Team team = null;
        if (updateDTO.getTeamId() != null) {
            team = teamRepository.findById(updateDTO.getTeamId()).orElseThrow(
                    () -> new AppObjectNotFoundException("TEAM ", "Team with id " + updateDTO.getTeamId() +
                            " not found"));

            // Only MANAGER or ADMIN can assign to team
            if (currentUser.getRole() == RoleType.MANAGER &&
                    !team.getManager().getId().equals(currentUser.getId())) {
                throw new AppObjectInvalidArgumentException("USER ", "Manager can only assign users to their own team");
            }

        }

        Mapper.updateUserEntity(userToUpdate, updateDTO, team);
        User savedUser = userRepository.save(userToUpdate);
        return Mapper.mapToUserReadOnlyDTO(savedUser);
    }

    @Override
    public List<UserReadOnlyDTO> getUsersByTeamId(Long teamId, String requesterUsername) {

        User currentUser = getUserByUsername(requesterUsername);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + teamId + " not found"));

        // Role-based access control
        if (currentUser.getRole() == RoleType.MEMBER) {
            //can access only its own team
            if (currentUser.getTeam() == null || !currentUser.getTeam().getId().equals(teamId)) {
                throw new AppObjectInvalidArgumentException("USER ", "Not authorized to access team members");
            }
        } else if (currentUser.getRole() == RoleType.MANAGER) {
            if (team.getManager() == null || !team.getManager().getId().equals(currentUser.getId())) {
                throw new AppObjectInvalidArgumentException("USER ", "Not authorized to access team");
            }
        }

        List<User> users = userRepository.findAllByTeam(team);
        return users.stream()
                .map(Mapper::mapToUserReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String requesterUsername) {
        User currentUser = getUserByUsername(requesterUsername);

        User userToDelete = userRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with id " + id + " not found"));

        if (currentUser.getRole() == RoleType.MEMBER) {
            throw new AppObjectInvalidArgumentException("USER ", "Members are not allowed to delete users");
        }
        if (currentUser.getRole() == RoleType.MANAGER && (userToDelete.getTeam() == null ||
                !userToDelete.getTeam().getManager().getId().equals(currentUser.getId()))) {
            throw new AppObjectInvalidArgumentException("USER ", "Manager can only delete users from their own team");
        }
        userRepository.delete(userToDelete);
    }

    @Override
    public UserReadOnlyDTO getUserById(Long id, String requesterUsername) {
        User currentUser = getUserByUsername(requesterUsername);

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with id " + id + " not found"));

        if (currentUser.getRole() == RoleType.ADMIN || currentUser.getId().equals(user.getId())) {
            return Mapper.mapToUserReadOnlyDTO(user);
        }

        if (currentUser.getRole() == RoleType.MANAGER &&
            currentUser.getTeam() != null &&
            user.getTeam() != null &&
            currentUser.getTeam().getId().equals(user.getTeam().getId())) {
            return Mapper.mapToUserReadOnlyDTO(user); // Same team
        }

        throw new AppObjectInvalidArgumentException("USER ", "Not authorized to view this user");
    }

    @Override
    public UserReadOnlyDTO getUserByUuid(String uuid, String requesterUsername) {
        User currentUser = getUserByUsername(requesterUsername);

        User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with uuid " + uuid + " not found"));

        if (currentUser.getRole() == RoleType.ADMIN || currentUser.getUuid().equals(user.getUuid())) {
            return Mapper.mapToUserReadOnlyDTO(user);
        }

        if (currentUser.getRole() == RoleType.MANAGER &&
            currentUser.getTeam() != null &&
            user.getTeam() != null &&
            currentUser.getTeam().getId().equals(user.getTeam().getId())) {
            return Mapper.mapToUserReadOnlyDTO(user);
        }

        throw new AppObjectInvalidArgumentException("USER", "Not authorized to view this user");
    }

    @Override
    public List<UserReadOnlyDTO> getAllUsers(String requesterUsername) {
        User currentUser = getUserByUsername(requesterUsername);

        if (currentUser.getRole() == RoleType.ADMIN) {
            return userRepository.findAll()
                    .stream()
                    .map(Mapper::mapToUserReadOnlyDTO)
                    .collect(Collectors.toList());
        }

        if (currentUser.getRole() == RoleType.MANAGER && currentUser.getTeam() != null) {
            return userRepository.findAllByTeam(currentUser.getTeam())
                    .stream()
                    .map(Mapper::mapToUserReadOnlyDTO)
                    .collect(Collectors.toList());
        }

        if (currentUser.getRole() == RoleType.MEMBER) {
            return List.of(Mapper.mapToUserReadOnlyDTO(currentUser));
        }

        throw new AppObjectInvalidArgumentException("USER ", "Not authorized to access all users");

    }

    // Helper method to get the authenticated user
    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Authenticated user not found"));
    }
}

