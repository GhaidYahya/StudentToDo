package com.example.studo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Task {
    private String description;
    private boolean done;
    private String prefKey;

    public Task(String description) {
        this.description = description;
        this.done = false;
        this.prefKey = "task_" + description.hashCode();
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void toggleDone() {
        done = !done;
    }

    public void saveToSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(prefKey, done);
        editor.apply();
    }

    public void loadFromSharedPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        setDone(prefs.getBoolean(prefKey, false));
    }
}
