package com.taskflow.backend.authentication;

import com.taskflow.backend.core.exceptions.AppObjectAlreadyExistsException;
import com.taskflow.backend.core.exceptions.AppObjectInvalidArgumentException;
import com.taskflow.backend.core.exceptions.AppObjectNotFoundException;
import com.taskflow.backend.dto.AuthenticationRequestDTO;
import com.taskflow.backend.dto.AuthenticationResponseDTO;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserRegisterDTO;
import com.taskflow.backend.mapper.Mapper;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.UserRepository;
import com.taskflow.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponseDTO register(UserRegisterDTO registerDTO) {

        // Check if username already exists
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new AppObjectAlreadyExistsException("USER ", "User with username " + registerDTO.getUsername() +
                    " already exists");
        }

        // Check if afm already exists
        if (userRepository.findByAfm(registerDTO.getAfm()).isPresent()) {
            throw new AppObjectAlreadyExistsException("USER ", "User with afm " + registerDTO.getAfm() +
                    " already exists");
        }

        // Check if phone number already exists
        if (userRepository.findByPhoneNumber(registerDTO.getPhoneNumber()).isPresent()) {
            throw new AppObjectAlreadyExistsException("USER ", "User with phone " + registerDTO.getPhoneNumber()
                    + " already exists");
        }

        // Validate password and confirm password
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new AppObjectInvalidArgumentException("USER", "Password and Confirm Password do not match");
        }

        User user = Mapper.mapToUserEntity(registerDTO);

        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .lastname(user.getLastname())
                .firstname(user.getFirstname())
                .build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User not authorized"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AppObjectInvalidArgumentException("USER ", "Invalid password");
        }

        String token = jwtService.generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .lastname(user.getLastname())
                .firstname(user.getFirstname())
                .build();
    }


}
