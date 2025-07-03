package com.taskflow.backend.service;

import com.taskflow.backend.dto.TeamInsertDTO;
import com.taskflow.backend.dto.TeamReadOnlyDTO;
import com.taskflow.backend.dto.TeamUpdateDTO;

import java.util.List;

public interface ITeamService {

    TeamReadOnlyDTO createTeam(TeamInsertDTO insertDTO, String creatorUsername);
    TeamReadOnlyDTO updateTeam(Long id, TeamUpdateDTO updateDTO, String updaterUsername);
    void deleteTeam(Long id, String requesterUsername);
    TeamReadOnlyDTO getTeamById(Long id, String requesterUsername);
    List<TeamReadOnlyDTO> getAllTeams(String requesterUsername);
    List<TeamReadOnlyDTO> getMyTeam(String requesterUsername);
    TeamReadOnlyDTO addMemberToTeam(Long teamId, Long userId, String requesterUsername);
}
