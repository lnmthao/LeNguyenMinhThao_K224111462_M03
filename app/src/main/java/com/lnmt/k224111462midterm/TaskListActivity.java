package com.lnmt.k224111462midterm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.lnmt.R;
import com.lnmt.adapters.TaskAdapter;
import com.lnmt.models.Task;

import java.util.ArrayList;

public class TaskListActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ListView listViewTasks;
    private Button btnCreateTask;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        listViewTasks = findViewById(R.id.listViewTasks);
        btnCreateTask = findViewById(R.id.btnCreateTask);

        int userId = getIntent().getIntExtra("user_id", -1);

        db = openOrCreateDatabase("MidtermDatabase.sqlite", MODE_PRIVATE, null);
        loadTasks(userId);

        btnCreateTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, CreateTaskActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });
    }

    private void loadTasks(int adminId) {
        taskList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM TaskForTeleSales WHERE AccountID = ?", new String[]{String.valueOf(adminId)});
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("ID"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("TaskTitle"));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("DateAssigned"));
            @SuppressLint("Range") boolean isCompleted = cursor.getInt(cursor.getColumnIndex("IsCompleted")) == 1;

            taskList.add(new Task(id, title, date, isCompleted));
        }
        cursor.close();

        taskAdapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(taskAdapter);
    }
}
