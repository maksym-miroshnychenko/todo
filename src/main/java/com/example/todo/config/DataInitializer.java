package com.example.todo.config;

import com.example.todo.model.Task;
import com.example.todo.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(TaskRepository taskRepository) {
        return args -> {
            if (taskRepository.count() == 0) {
                taskRepository.saveTask(new Task(UUID.randomUUID().toString(), "Eat", false));
                taskRepository.saveTask(new Task(UUID.randomUUID().toString(), "Wake up", true));
            }
        };
    }
}
