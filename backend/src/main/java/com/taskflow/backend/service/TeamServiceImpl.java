package com.taskflow.backend.service;

import com.taskflow.backend.core.exceptions.AppObjectAlreadyExistsException;
import com.taskflow.backend.core.exceptions.AppObjectInvalidArgumentException;
import com.taskflow.backend.core.exceptions.AppObjectNotFoundException;
import com.taskflow.backend.core.exceptions.ValidationException;
import com.taskflow.backend.dto.TeamInsertDTO;
import com.taskflow.backend.dto.TeamReadOnlyDTO;
import com.taskflow.backend.dto.TeamUpdateDTO;
import com.taskflow.backend.mapper.Mapper;
import com.taskflow.backend.model.Team;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.TeamRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements ITeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TeamReadOnlyDTO createTeam(TeamInsertDTO insertDTO) {
        if (teamRepository.existsByName(insertDTO.getName())) {
            throw new AppObjectAlreadyExistsException("TEAM ", "Team with name " + insertDTO.getName() + " already exists");
        }

        User manager = userRepository.findById(insertDTO.getManagerId())
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "Manager with id " + insertDTO.getManagerId()
                + " not found"));

        Set<User> members = new HashSet<>();
        if (insertDTO.getMemberIds() != null && !insertDTO.getMemberIds().isEmpty()) {
            members = new HashSet<>(userRepository.findAllById(insertDTO.getMemberIds()));

            if (members.size() != insertDTO.getMemberIds().size()) {
                throw new AppObjectInvalidArgumentException("USER ", "One or more user IDs in members not found");
            }
        }


        Team team = Mapper.mapToTeamEntity(insertDTO, manager, members);
        teamRepository.save(team);

        return Mapper.mapToTeamReadOnlyDTO(team);
    }

    @Override
    @Transactional
    public TeamReadOnlyDTO updateTeam(Long id, TeamUpdateDTO updateDTO) {

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + id + " not found"));

        // Check if the name of team has changed and there is already another team with the same name
        if (!team.getName().equals(updateDTO.getName()) && teamRepository.existsByName(updateDTO.getName())) {
            throw new AppObjectAlreadyExistsException("TEAM ", "Team with name " + updateDTO.getName() + " already exists");
        }

        //Fetch manager
        User manager = userRepository.findById(updateDTO.getManagerId())
                .orElseThrow(() -> new AppObjectNotFoundException("USER ", "User with id " + updateDTO.getManagerId()
                + " not found"));

        //Fetch members
        Set<User> members = new HashSet<>();
        if (updateDTO.getMemberIds() != null && !updateDTO.getMemberIds().isEmpty()) {
            List<User> users = userRepository.findAllById(updateDTO.getMemberIds());

            if (users.size() != updateDTO.getMemberIds().size()) {
                throw new AppObjectInvalidArgumentException("USER ", "One or more user IDs in members not found");
            }

            members.addAll(users);
        }

        Mapper.updateTeamEntity(team, updateDTO, manager, members);
        Team savedTeam = teamRepository.save(team);
        return Mapper.mapToTeamReadOnlyDTO(savedTeam);
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + id + " not found"));
        teamRepository.delete(team);
    }

    @Override
    public TeamReadOnlyDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("TEAM ", "Team with id " + id +
                        " not found"));

        return Mapper.mapToTeamReadOnlyDTO(team);
    }

    @Override
    public List<TeamReadOnlyDTO> getAllTeams() {

        return teamRepository.findAll()
                .stream()
                .map(Mapper::mapToTeamReadOnlyDTO)
                .collect(Collectors.toList());
    }
}
