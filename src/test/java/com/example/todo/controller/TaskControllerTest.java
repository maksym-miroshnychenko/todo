package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import com.example.todo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    private Task completedTask;
    private Task uncompletedTask;

    @BeforeEach
    void setUp() {
        // Очищаем репозиторий от тестовых данных
        List<Task> initialTasks = taskRepository.findAll();
        for (Task task : initialTasks) {
            taskRepository.deleteById(task.getId());
        }
        
        // Создаем тестовые задачи
        completedTask = new Task("1", "Task 1", true);
        uncompletedTask = new Task("2", "Task 2", false);
        
        // Сохраняем тестовые задачи
        taskRepository.saveTask(completedTask);
        taskRepository.saveTask(uncompletedTask);
    }

    @Test
    void getTasks_shouldReturnTaskListView() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("taskList"))
                .andExpect(model().attributeExists("tasks"));
    }

    @Test
    void getTasks_shouldReturnNoTasksView() throws Exception {
        // Удаляем все задачи
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            taskRepository.deleteById(task.getId());
        }
        
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("noTasks"));
    }

    @Test
    void getTasks_shouldIncludeErrorMessage() throws Exception {
        mockMvc.perform(get("/tasks").param("error", "Test error"))
                .andExpect(status().isOk())
                .andExpect(view().name("taskList"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Test error"));
    }

    @Test
    void showTasksForm_shouldReturnTaskFormView() throws Exception {
        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("taskForm"))
                .andExpect(model().attributeExists("task"));
    }

    @Test
    void saveTask_shouldRedirectToTasksPage() throws Exception {
        int initialCount = taskRepository.findAll().size();
        
        mockMvc.perform(post("/task/save")
                .param("description", "New Test Task")
                .param("completed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
        
        // Проверяем, что задача была добавлена
        List<Task> tasks = taskRepository.findAll();
        assertEquals(initialCount + 1, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getDescription().equals("New Test Task")));
    }

    @Test
    void toggleTaskStatus_shouldRedirectToTasksPage() throws Exception {
        // Проверяем начальный статус
        assertFalse(taskRepository.findById("2").get().isCompleted());
        
        mockMvc.perform(post("/task/toggle/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
        
        // Проверяем, что статус изменился
        assertTrue(taskRepository.findById("2").get().isCompleted());
    }

    @Test
    void toggleTaskStatus_shouldRedirectWithErrorWhenTaskNotFound() throws Exception {
        mockMvc.perform(post("/task/toggle/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks?error=Task not found with id: 999"));
    }

    @Test
    void deleteTask_shouldRedirectToTasksPage() throws Exception {
        // Проверяем, что задача существует
        assertTrue(taskRepository.findById("1").isPresent());
        
        mockMvc.perform(post("/task/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
        
        // Проверяем, что задача была удалена
        assertFalse(taskRepository.findById("1").isPresent());
    }

    @Test
    void deleteTask_shouldRedirectWithErrorWhenTaskCannotBeDeleted() throws Exception {
        // Проверяем, что задача существует и не завершена
        assertTrue(taskRepository.findById("2").isPresent());
        assertFalse(taskRepository.findById("2").get().isCompleted());
        
        mockMvc.perform(post("/task/delete/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks?error=You cannot remove uncompleted task!"));
        
        // Проверяем, что задача не была удалена
        assertTrue(taskRepository.findById("2").isPresent());
    }
}
