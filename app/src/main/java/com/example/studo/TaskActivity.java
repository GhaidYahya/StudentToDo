package com.example.studo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private Course course;
    private List<Task> tasks;
    private ArrayAdapter<Task> taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        String courseJson = getIntent().getStringExtra("course");
        course = new Gson().fromJson(courseJson, Course.class);
        String title = getIntent().getStringExtra("title");
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(title + " To-Do");
        tasks = loadTasksFromLocalStorage(course);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });

        taskAdapter = new ArrayAdapter<Task>(this, R.layout.task_list, tasks) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = inflater.inflate(R.layout.task_list, parent, false);
                }

                Task currentTask = getItem(position);

                TextView taskDescriptionTextView = convertView.findViewById(R.id.taskDescriptionTextView);
                taskDescriptionTextView.setText(currentTask.getDescription());
                CheckBox checkBoxTask = convertView.findViewById(R.id.checkBoxTask);
                checkBoxTask.setChecked(currentTask.isDone());
                int textColor = currentTask.isDone() ? Color.GRAY : Color.BLACK;
                taskDescriptionTextView.setTextColor(textColor);
                checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    currentTask.setDone(isChecked);
                    saveTasksToLocalStorage(course, tasks);
                    int newTextColor = isChecked ? Color.GRAY : Color.BLACK;
                    taskDescriptionTextView.setTextColor(newTextColor);
                });

                return convertView;
            }
        };

        ListView taskListView = findViewById(R.id.listViewTasks);
        taskListView.setAdapter(taskAdapter);

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            tasks.get(position).toggleDone();
            saveTasksToLocalStorage(course, tasks);
            taskAdapter.notifyDataSetChanged();
        });
    }

    public void addTask(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add :)", (dialog, which) -> {
            String taskDescription = input.getText().toString();
            if (!taskDescription.isEmpty()) {

                Task newTask = new Task(taskDescription);
                tasks.add(newTask);
                taskAdapter.notifyDataSetChanged();
                saveTasksToLocalStorage(course, tasks);
            } else {
                Toast.makeText(TaskActivity.this, "Task description can't be empty :(", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private List<Task> loadTasksFromLocalStorage(Course course) {
        String key = "tasks_" + course.getName();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tasksJson = prefs.getString(key, "");

        Gson gson = new Gson();
        Type taskListType = new TypeToken<List<Task>>() {}.getType();
        List<Task> loadedTasks = gson.fromJson(tasksJson, taskListType);

        Log.d("TaskActivity", "Loaded Tasks: " + loadedTasks);

        if (loadedTasks == null) {
            loadedTasks = new ArrayList<>();
        }

        return loadedTasks;
    }

    private void saveTasksToLocalStorage(Course course, List<Task> tasks) {
        String key = "tasks_" + course.getName();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String tasksJson = gson.toJson(tasks);

        editor.putString(key, tasksJson);
        editor.apply();
    }
}
