package com.example.personalexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, com.example.personalexpensemanager.activities.LoginActivity.class);
        startActivity(intent);
        finish();
    }
}