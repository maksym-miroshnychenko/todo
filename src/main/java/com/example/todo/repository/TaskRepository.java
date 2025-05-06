package com.example.todo.repository;

import com.example.todo.model.Task;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TaskRepository {

    private static final Map<String, Task> tasks = new HashMap<>();

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteById(String id) {
        tasks.remove(id);
    }

    public Task saveTask(Task task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public long count() {
        return tasks.size();
    }
}