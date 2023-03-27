package com.alpha.RealityEnhance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LibraryActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);

        Button backButton = findViewById(R.id.backButton);
        Button qrButton = findViewById(R.id.scanButton);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }
}
