package com.taskflow.backend.service;

import com.taskflow.backend.dto.UserRegisterDTO;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserUpdateDTO;

import java.util.List;

public interface IUserService {
    UserReadOnlyDTO updateUser(Long id, UserUpdateDTO updateDTO, String requesterUsername);
    void deleteUser(Long id, String requesterUsername);
    UserReadOnlyDTO getUserById(Long id, String requesterUsername);
    UserReadOnlyDTO getUserByUuid(String uuid, String requesterUsername);
    List<UserReadOnlyDTO> getAllUsers(String requesterUsername);
    List<UserReadOnlyDTO> getUsersByTeamId(Long teamId, String requesterUsername);
}
