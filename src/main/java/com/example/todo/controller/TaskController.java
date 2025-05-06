package com.example.todo.controller;

import com.example.todo.model.Task;
import com.example.todo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/tasks")
    public String getTasks(@RequestParam Optional<String> error, Model model) {
        List<Task> tasks = taskService.findAll();
        if(tasks.isEmpty()) {
            return "noTasks";
        }
        model.addAttribute("tasks", tasks);
        error.ifPresent(s -> model.addAttribute("error", s));
        return "taskList";
    }

    @GetMapping("/tasks/new")
    public String showTasksForm(Model model) {
        model.addAttribute("task", new Task());
        return "taskForm";
    }

    @PostMapping("/task/save")
    public String saveTask(@ModelAttribute Task task, BindingResult result) {
        if(result.hasErrors()) {
            return "taskForm";
        }
        taskService.createTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/task/toggle/{id}")
    public String toggleTaskStatus(@PathVariable String id) {
        try {
            taskService.toggleTaskStatus(id);
            return "redirect:/tasks";
        } catch (IllegalArgumentException e) {
            return "redirect:/tasks?error=Task not found with id: " + id;
        }
    }

    @PostMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable String id) {
        Optional<String> error = taskService.removeTask(id);
        return error.map(s -> "redirect:/tasks?error=" + s).orElse("redirect:/tasks");
    }
}
