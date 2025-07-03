package com.taskflow.backend.rest;

import com.taskflow.backend.core.exceptions.ValidationException;
import com.taskflow.backend.dto.TeamInsertDTO;
import com.taskflow.backend.dto.TeamReadOnlyDTO;
import com.taskflow.backend.dto.TeamUpdateDTO;
import com.taskflow.backend.model.User;
import com.taskflow.backend.service.ITeamService;
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
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController {

    private final ITeamService teamService;

    /**
     * Create a new team
     * Only ADMIN or MANAGER can create a team
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TeamReadOnlyDTO> createTeam(
            @RequestBody @Valid TeamInsertDTO insertDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TeamReadOnlyDTO createdTeam = teamService.createTeam(insertDTO, principal.getName());
        URI location = URI.create("/api/teams/" + createdTeam.getId());

        return ResponseEntity.created(location).body(createdTeam);
    }

    /**
     * Create a new team
     * Only ADMIN or MANAGER can create a team
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TeamReadOnlyDTO> updateTeam(
            @PathVariable Long id,
            @RequestBody @Valid TeamUpdateDTO updateDTO,
            BindingResult bindingResult,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TeamReadOnlyDTO updatedTeam = teamService.updateTeam(id, updateDTO, principal.getName());
        return ResponseEntity.ok(updatedTeam);
    }

    /**
     * Update an existing team
     * Only ADMIN or the MANAGER of the team can update it
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id, Principal principal) {

        teamService.deleteTeam(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    /**
     * Get a team by ID
     * ADMIN: any team
     * MANAGER: only if manager of that team
     * MEMBER: only if belongs to that team
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<TeamReadOnlyDTO> getTeamById(@PathVariable Long id, Principal principal) {

        TeamReadOnlyDTO team = teamService.getTeamById(id, principal.getName());
        return ResponseEntity.ok(team);
    }

    /**
     * Get all teams
     * ADMIN: all teams
     * MANAGER: only managed teams
     * MEMBER: only their own team
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<List<TeamReadOnlyDTO>> getAllTeams(Principal principal) {

        List<TeamReadOnlyDTO> teams = teamService.getAllTeams(principal.getName());
        return ResponseEntity.ok(teams);
    }

    /**
     * Add a member to a team
     * Only ADMIN or the MANAGER of the team can add members
     */
    @PostMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TeamReadOnlyDTO> addMemberToTeam(@PathVariable Long teamId,
                                                           @PathVariable Long userId,
                                                           Principal principal) {

        TeamReadOnlyDTO updatedTeam = teamService.addMemberToTeam(teamId, userId, principal.getName());
        return ResponseEntity.ok(updatedTeam);
    }


}
