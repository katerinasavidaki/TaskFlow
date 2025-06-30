package com.taskflow.backend.service;

import com.taskflow.backend.dto.TeamInsertDTO;
import com.taskflow.backend.dto.TeamReadOnlyDTO;
import com.taskflow.backend.dto.TeamUpdateDTO;

import java.util.List;

public interface ITeamService {

    TeamReadOnlyDTO createTeam(TeamInsertDTO insertDTO);
    TeamReadOnlyDTO updateTeam(Long id, TeamUpdateDTO updateDTO);
    void deleteTeam(Long id);
    TeamReadOnlyDTO getTeamById(Long id);
    List<TeamReadOnlyDTO> getAllTeams();
}
