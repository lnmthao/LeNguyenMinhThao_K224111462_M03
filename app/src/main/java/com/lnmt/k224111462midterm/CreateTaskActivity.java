package com.lnmt.k224111462midterm;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lnmt.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText edtTaskTitle;
    private Spinner spinnerEmployee;
    private Button btnSelectCustomers, btnCreateTask;
    private SQLiteDatabase db;

    private ArrayList<Integer> selectedCustomerIDs = new ArrayList<>();
    private ArrayList<String> customerNames = new ArrayList<>();
    private ArrayList<Integer> customerIDs = new ArrayList<>();
    private ArrayList<String> employeeNames = new ArrayList<>();
    private ArrayList<Integer> employeeIDs = new ArrayList<>();

    private int selectedEmployeeID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        edtTaskTitle = findViewById(R.id.edtTaskTitle);
        spinnerEmployee = findViewById(R.id.spinnerEmployee);
        btnSelectCustomers = findViewById(R.id.btnSelectCustomers);
        btnCreateTask = findViewById(R.id.btnCreateTask);

        db = openOrCreateDatabase("MidtermDatabase.sqlite", MODE_PRIVATE, null);

        loadEmployees();
        loadCustomers();

        btnSelectCustomers.setOnClickListener(v -> showCustomerSelectionDialog());

        btnCreateTask.setOnClickListener(v -> createTask());
    }

    private void loadEmployees() {
        Cursor cursor = db.rawQuery("SELECT ID, Username FROM Account WHERE TypeOfAccount = 2", null);
        while (cursor.moveToNext()) {
            employeeIDs.add(cursor.getInt(0));
            employeeNames.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmployee.setAdapter(adapter);
    }

    private void loadCustomers() {
        Cursor cursor = db.rawQuery("SELECT ID, Name FROM Customer", null);
        while (cursor.moveToNext()) {
            customerIDs.add(cursor.getInt(0));
            customerNames.add(cursor.getString(1));
        }
        cursor.close();
    }

    private void showCustomerSelectionDialog() {
        selectedCustomerIDs.clear();

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < customerIDs.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        for (int i = 0; i < Math.min(5, indices.size()); i++) {
            selectedCustomerIDs.add(customerIDs.get(indices.get(i)));
        }

        Toast.makeText(this, "Randomly selected " + selectedCustomerIDs.size() + " customers", Toast.LENGTH_SHORT).show();
    }

    private void createTask() {
        String taskTitle = edtTaskTitle.getText().toString().trim();
        int employeeIndex = spinnerEmployee.getSelectedItemPosition();

        if (taskTitle.isEmpty() || employeeIndex == -1 || selectedCustomerIDs.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select customers", Toast.LENGTH_SHORT).show();
            return;
        }

        int employeeId = employeeIDs.get(employeeIndex);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ContentValues taskValues = new ContentValues();
        taskValues.put("AccountID", employeeId);
        taskValues.put("TaskTitle", taskTitle);
        taskValues.put("DateAssigned", currentDate);
        taskValues.put("IsCompleted", 0);

        long taskId = db.insert("TaskForTeleSales", null, taskValues);

        for (int customerId : selectedCustomerIDs) {
            ContentValues detail = new ContentValues();
            detail.put("TaskForTeleSalesID", taskId);
            detail.put("CustomerID", customerId);
            detail.put("IsCalled", 0);
            db.insert("TaskForTeleSalesDetails", null, detail);
        }

        Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateTaskActivity.this, TaskListActivity.class);
        startActivity(intent);
        finish();
    }
}
