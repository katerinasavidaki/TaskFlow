package com.taskflow.backend.rest;

import com.taskflow.backend.core.exceptions.ValidationException;
import com.taskflow.backend.dto.TaskInsertDTO;
import com.taskflow.backend.dto.TaskReadOnlyDTO;
import com.taskflow.backend.dto.TaskUpdateDTO;
import com.taskflow.backend.service.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    @PostMapping
    public ResponseEntity<TaskReadOnlyDTO> createTask(@Valid @RequestBody TaskInsertDTO insertDTO,
                                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TaskReadOnlyDTO createdTask = taskService.createTask(insertDTO);

        URI location = URI.create("/api/tasks" + createdTask.getId());
        return ResponseEntity.created(location).body(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskReadOnlyDTO> getTaskById(@PathVariable Long id) {

        TaskReadOnlyDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<TaskReadOnlyDTO>> getAllTasks() {
        List<TaskReadOnlyDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<TaskReadOnlyDTO> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody TaskUpdateDTO updateDTO,
                                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        TaskReadOnlyDTO updatedTask = taskService.updateTask(id, updateDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }
}
