package com.lnmt.k224111462midterm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.lnmt.R;
import com.lnmt.adapters.CallAdapter;
import com.lnmt.connectors.SQLiteConnector;
import com.lnmt.models.TaskDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CallTaskActivity extends AppCompatActivity {
    private ListView listView;
    private SQLiteDatabase db;
    private CallAdapter adapter;
    private ArrayList<TaskDetail> details;
    private int employeeId;
    private int taskId;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        listView = findViewById(R.id.listViewCustomerCalls);
        db = new SQLiteConnector(this).openDatabase();

        Intent intent = getIntent();
        employeeId = intent.getIntExtra("user_id", -1);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor taskCursor = db.rawQuery("SELECT * FROM TaskForTeleSales WHERE EmployeeID = ? AND DateAssigned = ?", new String[]{String.valueOf(employeeId), today});
        if (taskCursor.moveToFirst()) {
            taskId = taskCursor.getInt(taskCursor.getColumnIndex("ID"));
            loadTaskDetails(taskId);
        } else {
            Toast.makeText(this, "No task assigned today", Toast.LENGTH_SHORT).show();
            finish();
        }
        taskCursor.close();
    }

    @SuppressLint("Range")
    private void loadTaskDetails(int taskId) {
        details = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM TaskForTeleSalesDetails WHERE TaskID = ?", new String[]{String.valueOf(taskId)});
        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("ID"));
            @SuppressLint("Range") int customerId = cursor.getInt(cursor.getColumnIndex("CustomerID"));
            @SuppressLint("Range") int isCalled = cursor.getInt(cursor.getColumnIndex("IsCalled"));

            Cursor c = db.rawQuery("SELECT * FROM Customer WHERE ID = ?", new String[]{String.valueOf(customerId)});
            String name = "", phone = "";
            if (c.moveToFirst()) {
                name = c.getString(c.getColumnIndex("CustomerName"));
                phone = c.getString(c.getColumnIndex("PhoneNumber"));
            }
            c.close();

            TaskDetail detail = new TaskDetail(id, taskId, customerId, isCalled);
            detail.setCustomerName(name);
            detail.setPhoneNumber(phone);
            details.add(detail);
        }
        cursor.close();

        adapter = new CallAdapter(this, details, db, taskId);
        listView.setAdapter(adapter);
    }
}
