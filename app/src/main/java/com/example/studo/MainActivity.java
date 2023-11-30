package com.example.studo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Course> courses;
    private ArrayAdapter<String> courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courses = loadCoursesFromLocalStorage();
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView courseListView = findViewById(R.id.listView);
        courseListView.setAdapter(courseAdapter);

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course selectedCourse = courses.get(position);
                openTaskActivity(selectedCourse);
            }
        });


        courseListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
                return true;
            }
        });

        updateCourseListView();
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Course");
        builder.setMessage("Are you sure you want to delete this course?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCourse(position);
                updateCourseListView();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteCourse(int position) {
        if (position >= 0 && position < courses.size()) {
            courses.remove(position);
            saveCoursesToLocalStorage();
        }
    }




    private List<Course> loadCoursesFromLocalStorage() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String coursesJson = prefs.getString("courses", "");

        Gson gson = new Gson();
        Type courseListType = new TypeToken<List<Course>>() {}.getType();
        List<Course> loadedCourses = gson.fromJson(coursesJson, courseListType);
        Log.d("MainActivity", "Loaded Courses: " + loadedCourses);

        return loadedCourses;
    }




    public void addCourse(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name your course:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        builder.setPositiveButton("OK :)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String courseName = input.getText().toString();
                if (!courseName.isEmpty()) {

                    Course newCourse = new Course(courseName);
                    courses.add(newCourse);

                    updateCourseListView();


                    saveCoursesToLocalStorage();
                } else {

                    Toast.makeText(MainActivity.this, "Course name can't be empty :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void openTaskActivity(Course course) {
        try {

            Intent intent = new Intent(this, TaskActivity.class);

            intent.putExtra("course", new Gson().toJson(course));


            intent.putExtra("title", course.getName());


            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error opening TaskActivity", e);


            Toast.makeText(this, "Error opening TaskActivity", Toast.LENGTH_SHORT).show();
        }
    }




    private void updateCourseListView() {
        courseAdapter.clear();
        for (Course course : courses) {
            courseAdapter.add(course.getName());
        }
        courseAdapter.notifyDataSetChanged();
    }

    private void saveCoursesToLocalStorage() {
        // Save courses to SharedPreferences using Gson
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String coursesJson = gson.toJson(courses);

        editor.putString("courses", coursesJson);
        editor.apply();
    }

}