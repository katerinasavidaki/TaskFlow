package com.taskflow.backend.rest;

import com.taskflow.backend.core.exceptions.ValidationException;
import com.taskflow.backend.dto.UserRegisterDTO;
import com.taskflow.backend.dto.UserReadOnlyDTO;
import com.taskflow.backend.dto.UserUpdateDTO;
import com.taskflow.backend.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<UserReadOnlyDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO updateDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        UserReadOnlyDTO updatedUser = userService.updateUser(id, updateDTO, principal.getName());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserReadOnlyDTO>> getAllUsers(Principal principal) {
        List<UserReadOnlyDTO> users = userService.getAllUsers(principal.getName());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<UserReadOnlyDTO> getUserById(@PathVariable Long id, Principal principal) {

        UserReadOnlyDTO user = userService.getUserById(id, principal.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserReadOnlyDTO>> getUsersByTeam(@PathVariable Long teamId, Principal principal) {

        return ResponseEntity.ok(userService.getUsersByTeamId(teamId, principal.getName()));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<UserReadOnlyDTO> getUserByUuid(@PathVariable String uuid, Principal principal) {

        UserReadOnlyDTO user = userService.getUserByUuid(uuid, principal.getName());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {

        userService.deleteUser(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
