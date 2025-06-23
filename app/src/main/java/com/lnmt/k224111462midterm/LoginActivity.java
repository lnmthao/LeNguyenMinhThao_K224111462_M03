package com.lnmt.k224111462midterm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lnmt.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnExit;
    private RadioButton rbAdmin, rbEmployee;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;

    private static final String DB_NAME = "MidtermDatabase.sqlite";
    private static final String PREF_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Thiết lập ActionBar để hiển thị menu
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Khởi tạo các view
        initViews();

        // Copy database và mở kết nối
        copyDatabaseFromAssets();
        db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        // Thiết lập các event listener
        setupEventListeners();

        // Tự động điền thông tin đã lưu
        loadSavedLoginInfo();
    }

    private void initViews() {
        etUsername = findViewById(R.id.edtUsername);
        etPassword = findViewById(R.id.edtPassword);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbEmployee = findViewById(R.id.rbEmployee);
        btnLogin = findViewById(R.id.btnLogin);
        btnExit = findViewById(R.id.btnExit);
    }

    private void setupEventListeners() {
        btnLogin.setOnClickListener(v -> login());
        btnExit.setOnClickListener(v -> finishAffinity());
    }

    private void loadSavedLoginInfo() {
        String savedUser = sharedPreferences.getString("saved_username", "");
        String savedPass = sharedPreferences.getString("saved_password", "");
        boolean isAdmin = sharedPreferences.getBoolean("is_admin", false);

        etUsername.setText(savedUser);
        etPassword.setText(savedPass);

        if (isAdmin) {
            rbAdmin.setChecked(true);
        } else {
            rbEmployee.setChecked(true);
        }
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!rbAdmin.isChecked() && !rbEmployee.isChecked()) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy vấn database
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM Account WHERE UserName=? AND Password=?",
                    new String[]{username, password});

            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int typeOfAccount = cursor.getInt(cursor.getColumnIndex("TypeOfAccount"));
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("ID"));

                boolean isAdmin = rbAdmin.isChecked();

                // Kiểm tra role có khớp không
                if ((isAdmin && typeOfAccount != 1) || (!isAdmin && typeOfAccount != 2)) {
                    Toast.makeText(this, "Please select the correct role matching the account",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lưu thông tin đăng nhập
                saveLoginInfo(username, password, isAdmin);

                // Chuyển đến activity tương ứng
                Intent intent;
                if (typeOfAccount == 1) { // Admin
                    intent = new Intent(LoginActivity.this, TaskListActivity.class);
                } else { // Employee
                    intent = new Intent(LoginActivity.this, CallTaskActivity.class);
                }

                intent.putExtra("user_id", userId);
                intent.putExtra("user_type", typeOfAccount);
                intent.putExtra("username", username);

                startActivity(intent);
                finish(); // Đóng LoginActivity

            } else {
                Toast.makeText(this, "Invalid login information", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("LOGIN_ERROR", "Error during login", e);
            Toast.makeText(this, "Login error occurred", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void saveLoginInfo(String username, String password, boolean isAdmin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_username", username);
        editor.putString("saved_password", password);
        editor.putBoolean("is_admin", isAdmin);
        editor.apply();
    }

    private void copyDatabaseFromAssets() {
        try {
            File dbFile = getDatabasePath(DB_NAME);
            if (!dbFile.exists()) {
                // Tạo thư mục nếu chưa có
                if (dbFile.getParentFile() != null) {
                    dbFile.getParentFile().mkdirs();
                }

                InputStream is = getAssets().open(DB_NAME);
                FileOutputStream os = new FileOutputStream(dbFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();

                Log.d("DATABASE", "Database copied successfully");
            } else {
                Log.d("DATABASE", "Database already exists");
            }
        } catch (Exception e) {
            Log.e("DATABASE_ERROR", "Error copying database", e);
            Toast.makeText(this, "Error loading database", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MENU_CHECK", "onCreateOptionsMenu called");
        try {
            getMenuInflater().inflate(R.menu.menu_login, menu);
            Log.d("MENU_CHECK", "Menu inflated successfully");
            return true;
        } catch (Exception e) {
            Log.e("MENU_ERROR", "Error creating menu", e);
            return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("MENU_CHECK", "onPrepareOptionsMenu called");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MENU_CHECK", "Menu item selected: " + item.getItemId());

        if (item.getItemId() == R.id.menu_about) {
            Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ACTIVITY", "LoginActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ACTIVITY", "LoginActivity paused");
    }
}