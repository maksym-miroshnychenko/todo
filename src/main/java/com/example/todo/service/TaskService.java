package com.example.todo.service;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        return taskRepository.saveTask(task);
    }

    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Task task) {
        return taskRepository.saveTask(task);
    }

    public Task toggleTaskStatus(String id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setCompleted(!task.isCompleted());
            return taskRepository.saveTask(task);
        }
        throw new IllegalArgumentException("Task not found with id: " + id);
    }

    public Optional<String> removeTask(String id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return Optional.of("Task is not found");
        } else if (!taskOpt.get().isCompleted()) {
            return Optional.of("You cannot remove uncompleted task!");
        } else {
            taskRepository.deleteById(id);
            return Optional.empty();
        }
    }
}