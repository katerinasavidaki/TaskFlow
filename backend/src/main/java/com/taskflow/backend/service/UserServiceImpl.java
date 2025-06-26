package com.taskflow.backend.service;

import com.taskflow.backend.core.exceptions.AppObjectAlreadyExistsException;
import com.taskflow.backend.core.exceptions.AppObjectInvalidArgumentException;
import com.taskflow.backend.core.exceptions.AppObjectNotFoundException;
import com.taskflow.backend.dto.UserInsertDTO;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserUpdateDTO;
import com.taskflow.backend.mapper.Mapper;
import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.TeamRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
//    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserReadOnlyDTO createUser(UserInsertDTO insertDTO) {

        // Check if username already exists
        if (userRepository.existsByUsername(insertDTO.getUsername())) {
            throw new AppObjectAlreadyExistsException("USER ", "User with username " + insertDTO.getUsername() +
                    " already exists");
        }

        // Check if afm already exists
        if (userRepository.findByAfm(insertDTO.getAfm()).isPresent()) {
            throw new AppObjectAlreadyExistsException("USER ", "User with afm " + insertDTO.getAfm() +
                    " already exists");
        }

        // Check if phone number already exists
        if (userRepository.findByPhoneNumber(insertDTO.getPhoneNumber()).isPresent()) {
            throw new AppObjectAlreadyExistsException("USER ", "User with phone " + insertDTO.getPhoneNumber()
            + " already exists");
        }

        // Validate password and confirm password
        if (!insertDTO.getPassword().equals(insertDTO.getConfirmPassword())) {
            throw new AppObjectInvalidArgumentException("USER", "Password and Confirm Password do not match");
        }

        // Handle optional team
        Team team = null;
        if (insertDTO.getTeamId() != null) {
            team = teamRepository.findById(insertDTO.getTeamId())
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + insertDTO.getTeamId()
                 + " not found"));
        }

//        insertDTO.setPassword(passwordEncoder.encode(insertDTO.getPassword()));

        User user = Mapper.mapToUserEntity(insertDTO, team);
        userRepository.save(user);
        return Mapper.mapToUserReadOnlyDTO(user);

    }

    @Override
    @Transactional
    public UserReadOnlyDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with id " + id + " not found"));

        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().isBlank()) {
            Optional<User> userWithSamePhone = userRepository.findByPhoneNumber(updateDTO.getPhoneNumber());
            if (userWithSamePhone.isPresent() && !userWithSamePhone.get().getId().equals(user.getId())) {
                throw new AppObjectInvalidArgumentException("USER ", "Phone number already exists in another user");
            }
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        Team team = null;
        if (updateDTO.getTeamId() != null) {
            team = teamRepository.findById(updateDTO.getTeamId()).orElseThrow(
                    () -> new AppObjectNotFoundException("TEAM ", "Team with id " + updateDTO.getTeamId() +
                            " not found"));
        }
        Mapper.updateUserEntity(user, updateDTO, team);
        return Mapper.mapToUserReadOnlyDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with id " + id + " not found"));
        userRepository.delete(user);
    }

    @Override
    public UserReadOnlyDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with id " + id + " not found"));

        return Mapper.mapToUserReadOnlyDTO(user);
    }

    @Override
    public UserReadOnlyDTO getUserByUuid(String uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new AppObjectNotFoundException("USER ", "User with uuid " + uuid + " not found"));
        return Mapper.mapToUserReadOnlyDTO(user);
    }

    @Override
    public List<UserReadOnlyDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(Mapper::mapToUserReadOnlyDTO)
                .collect(Collectors.toList());
    }
}
