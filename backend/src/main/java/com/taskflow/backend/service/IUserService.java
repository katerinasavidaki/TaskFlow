package com.taskflow.backend.service;

import com.taskflow.backend.dto.UserInsertDTO;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserUpdateDTO;

import java.util.List;

public interface IUserService {
    UserReadOnlyDTO createUser(UserInsertDTO insertDTO);
    UserReadOnlyDTO updateUser(Long id, UserUpdateDTO updateDTO);
    void deleteUser(Long id);
    UserReadOnlyDTO getUserById(Long id);
    UserReadOnlyDTO getUserByUuid(String uuid);
    List<UserReadOnlyDTO> getAllUsers();
}
