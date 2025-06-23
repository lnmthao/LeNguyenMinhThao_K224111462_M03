package com.lnmt.k224111462midterm;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lnmt.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView ivAvatar = findViewById(R.id.ivAvatar);

        ivAvatar.setImageResource(R.drawable.avatar); // ảnh avatar đặt trong res/drawable
    }
}
