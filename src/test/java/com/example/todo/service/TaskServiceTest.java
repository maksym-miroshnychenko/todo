package com.example.todo.service;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;
    private Task completedTask;
    private Task uncompletedTask;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
        taskService = new TaskService(taskRepository);

        // Очищаем репозиторий от тестовых данных, созданных в конструкторе
        List<Task> initialTasks = taskRepository.findAll();
        for (Task task : initialTasks) {
            taskRepository.deleteById(task.getId());
        }

        // Создаем тестовые задачи
        completedTask = new Task("1", "Completed Task", true);
        uncompletedTask = new Task("2", "Uncompleted Task", false);

        // Сохраняем тестовые задачи
        taskRepository.saveTask(completedTask);
        taskRepository.saveTask(uncompletedTask);
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        // Act
        List<Task> tasks = taskService.findAll();

        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getId().equals("1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getId().equals("2")));
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        // Arrange
        Task newTask = new Task(null, "New Test Task", false);

        // Act
        Task result = taskService.createTask(newTask);

        // Assert
        assertNotNull(result.getId());
        assertEquals("New Test Task", result.getDescription());
        assertFalse(result.isCompleted());

        // Verify task was added to repository
        Optional<Task> savedTask = taskRepository.findById(result.getId());
        assertTrue(savedTask.isPresent());
        assertEquals("New Test Task", savedTask.get().getDescription());
    }

    @Test
    void findById_shouldReturnTaskWhenExists() {
        // Act
        Optional<Task> result = taskService.findById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Completed Task", result.get().getDescription());
        assertTrue(result.get().isCompleted());
    }

    @Test
    void updateTask_shouldUpdateAndReturnTask() {
        // Arrange
        Task updatedTask = new Task("1", "Updated Task", true);

        // Act
        Task result = taskService.updateTask(updatedTask);

        // Assert
        assertEquals("1", result.getId());
        assertEquals("Updated Task", result.getDescription());

        // Verify task was updated in repository
        Optional<Task> savedTask = taskRepository.findById("1");
        assertTrue(savedTask.isPresent());
        assertEquals("Updated Task", savedTask.get().getDescription());
    }

    @Test
    void toggleTaskStatus_shouldToggleAndReturnTask() {
        // Act
        Task result = taskService.toggleTaskStatus("2");

        // Assert
        assertEquals("2", result.getId());
        assertTrue(result.isCompleted()); // Should be toggled from false to true

        // Verify task was updated in repository
        Optional<Task> savedTask = taskRepository.findById("2");
        assertTrue(savedTask.isPresent());
        assertTrue(savedTask.get().isCompleted());
    }

    @Test
    void toggleTaskStatus_shouldThrowExceptionWhenTaskNotFound() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskService.toggleTaskStatus("999");
        });

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    void removeTask_shouldReturnEmptyWhenTaskIsCompletedAndExists() {
        // Act
        Optional<String> result = taskService.removeTask("1");

        // Assert
        assertFalse(result.isPresent());

        // Verify task was removed from repository
        Optional<Task> deletedTask = taskRepository.findById("1");
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void removeTask_shouldReturnErrorWhenTaskIsNotCompleted() {
        // Act
        Optional<String> result = taskService.removeTask("2");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("You cannot remove uncompleted task!", result.get());

        // Verify task was not removed from repository
        Optional<Task> task = taskRepository.findById("2");
        assertTrue(task.isPresent());
    }

    @Test
    void removeTask_shouldReturnErrorWhenTaskNotFound() {
        // Act
        Optional<String> result = taskService.removeTask("999");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Task is not found", result.get());
    }
}
