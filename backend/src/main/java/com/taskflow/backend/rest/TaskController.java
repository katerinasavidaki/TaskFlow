package com.taskflow.backend.rest;

import com.taskflow.backend.core.enums.TaskStatusType;
import com.taskflow.backend.core.exceptions.ValidationException;
import com.taskflow.backend.dto.TaskInsertDTO;
import com.taskflow.backend.dto.TaskReadOnlyDTO;
import com.taskflow.backend.dto.TaskUpdateDTO;
import com.taskflow.backend.service.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskReadOnlyDTO> createTask(@Valid @RequestBody TaskInsertDTO insertDTO,
                                                      BindingResult bindingResult,
                                                      Principal principal) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TaskReadOnlyDTO createdTask = taskService.createTask(insertDTO, principal.getName());

        URI location = URI.create("/api/tasks" + createdTask.getId());
        return ResponseEntity.created(location).body(createdTask);
    }

    @PostMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<TaskReadOnlyDTO> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId,
            Principal principal) {

        return ResponseEntity.ok(taskService.assignTaskToUser(taskId, userId, principal.getName()));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<TaskReadOnlyDTO> markTaskAsCompleted(@PathVariable Long id, Principal principal) {

        return ResponseEntity.ok(taskService.markTaskAsCompleted(id, principal.getName()));
    }

    @GetMapping("/assigned-to/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<List<TaskReadOnlyDTO>> getTasksByAssignee(@PathVariable Long userId, Principal principal) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId, principal.getName()));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<List<TaskReadOnlyDTO>> getTasksByStatus(
            @RequestParam(name = "status") String status, Principal principal) {

        return ResponseEntity.ok(taskService.getTasksByStatus(TaskStatusType.valueOf(status.toUpperCase()), principal.getName()));
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<List<TaskReadOnlyDTO>> getCompletedTasks(
            @RequestParam(name = "isCompleted") Boolean isCompleted, Principal principal) {

        return ResponseEntity.ok(taskService.getCompletedTasks(isCompleted, principal.getName()));
    }

    @GetMapping("/created-by/{username}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskReadOnlyDTO>> getTasksByCreator(@PathVariable String username, Principal principal) {
        return ResponseEntity.ok(taskService.getTasksByCreatedBy(username, principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<TaskReadOnlyDTO> getTaskById(@PathVariable Long id, Principal principal) {

        TaskReadOnlyDTO task = taskService.getTaskById(id, principal.getName());
        return ResponseEntity.ok(task);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<List<TaskReadOnlyDTO>> getAllTasks(Principal principal) {
        List<TaskReadOnlyDTO> tasks = taskService.getAllTasks(principal.getName());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'MEMBER')")
    public ResponseEntity<TaskReadOnlyDTO> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody TaskUpdateDTO updateDTO,
                                                      BindingResult bindingResult,
                                                      Principal principal) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TaskReadOnlyDTO updatedTask = taskService.updateTask(id, updateDTO, principal.getName());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
        taskService.deleteTaskById(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
