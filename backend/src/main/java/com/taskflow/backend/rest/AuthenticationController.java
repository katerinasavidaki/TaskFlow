package com.taskflow.backend.rest;

import com.taskflow.backend.authentication.AuthenticationService;
import com.taskflow.backend.core.exceptions.ValidationException;
import com.taskflow.backend.dto.AuthenticationRequestDTO;
import com.taskflow.backend.dto.AuthenticationResponseDTO;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserRegisterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody @Valid UserRegisterDTO dto,
            BindingResult bindingResult) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "Match", "Passwords do not match");
        }

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        AuthenticationResponseDTO createdUser = authService.register(dto);

        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody @Valid AuthenticationRequestDTO request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        AuthenticationResponseDTO response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
