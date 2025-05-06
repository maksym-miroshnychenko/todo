package com.example.todo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void createTask_withConstructor() {
        Task task = new Task("1", "Test Task", true);
        
        assertEquals("1", task.getId());
        assertEquals("Test Task", task.getDescription());
        assertTrue(task.isCompleted());
    }

    @Test
    void createTask_withDefaultConstructor() {
        Task task = new Task();
        
        assertNull(task.getId());
        assertNull(task.getDescription());
        assertFalse(task.isCompleted());
    }

    @Test
    void setAndGetId() {
        Task task = new Task();
        task.setId("123");
        
        assertEquals("123", task.getId());
    }

    @Test
    void setAndGetDescription() {
        Task task = new Task();
        task.setDescription("New Description");
        
        assertEquals("New Description", task.getDescription());
    }

    @Test
    void setAndGetCompleted() {
        Task task = new Task();
        assertFalse(task.isCompleted());
        
        task.setCompleted(true);
        assertTrue(task.isCompleted());
        
        task.setCompleted(false);
        assertFalse(task.isCompleted());
    }
}
