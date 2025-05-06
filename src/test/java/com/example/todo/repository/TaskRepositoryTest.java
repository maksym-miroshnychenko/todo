package com.example.todo.repository;

import com.example.todo.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        assertNotNull(tasks);
        // Проверяем, что список задач не пустой
        assertFalse(tasks.isEmpty());
    }

    @Test
    void saveTask_shouldAddNewTask() {
        int initialSize = taskRepository.findAll().size();
        Task newTask = new Task(null, "New Test Task", false);

        Task savedTask = taskRepository.saveTask(newTask);

        assertNotNull(savedTask.getId());
        assertEquals("New Test Task", savedTask.getDescription());
        assertFalse(savedTask.isCompleted());
        assertEquals(initialSize + 1, taskRepository.findAll().size());
    }

    @Test
    void saveTask_shouldUpdateExistingTask() {
        Task task = taskRepository.findAll().get(0);
        String taskId = task.getId();
        task.setDescription("Updated Description");
        task.setCompleted(!task.isCompleted());

        Task updatedTask = taskRepository.saveTask(task);

        assertEquals(taskId, updatedTask.getId());
        assertEquals("Updated Description", updatedTask.getDescription());

        Optional<Task> retrievedTask = taskRepository.findById(taskId);
        assertTrue(retrievedTask.isPresent());
        assertEquals("Updated Description", retrievedTask.get().getDescription());
    }

    @Test
    void findById_shouldReturnTaskWhenExists() {
        Task task = taskRepository.findAll().get(0);
        String taskId = task.getId();

        Optional<Task> foundTask = taskRepository.findById(taskId);

        assertTrue(foundTask.isPresent());
        assertEquals(taskId, foundTask.get().getId());
    }

    @Test
    void findById_shouldReturnEmptyWhenTaskDoesNotExist() {
        String nonExistentId = UUID.randomUUID().toString();

        Optional<Task> foundTask = taskRepository.findById(nonExistentId);

        assertFalse(foundTask.isPresent());
    }

    @Test
    void deleteById_shouldRemoveTask() {
        // Создаем и сохраняем новую задачу
        Task newTask = new Task(null, "Task to delete", true);
        Task savedTask = taskRepository.saveTask(newTask);
        String taskId = savedTask.getId();

        // Проверяем, что задача существует
        assertTrue(taskRepository.findById(taskId).isPresent());
        int initialSize = taskRepository.findAll().size();

        // Удаляем задачу
        taskRepository.deleteById(taskId);

        // Проверяем, что задача удалена
        assertEquals(initialSize - 1, taskRepository.findAll().size());
        assertFalse(taskRepository.findById(taskId).isPresent());
    }

    @Test
    void count_shouldReturnCorrectNumberOfTasks() {
        long initialCount = taskRepository.count();

        taskRepository.saveTask(new Task(null, "Another Task", true));

        assertEquals(initialCount + 1, taskRepository.count());
    }
}
