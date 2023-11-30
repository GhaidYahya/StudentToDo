package com.example.studo;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String name;
    private List<Task> tasks;  // List to store ToDo tasks

    public Course(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();  // Initialize the task list
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Add a single task to the list
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Remove a single task from the list
    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
