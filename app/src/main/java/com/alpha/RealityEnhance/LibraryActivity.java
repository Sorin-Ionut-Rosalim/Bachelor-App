package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LibraryActivity extends AppCompatActivity {

    Button btn;
    Button newBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);

        ImageView qrButton = findViewById(R.id.qrButton);
        ImageView backButton = findViewById(R.id.backButton);


        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(LibraryActivity.this, QRActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        btn = findViewById(R.id.button);

        btn.setOnClickListener(view -> {
            addButton();
        });
    }

    private void addButton() {
        String path = Environment.getExternalStorageDirectory().toString() + "ass";
        Toast.makeText(LibraryActivity.this, path, Toast.LENGTH_SHORT).show();
        LinearLayout layout = findViewById(R.id.linear_Layout);
        newBtn = new Button(this);
        newBtn.setText("New Button");
        layout.addView(newBtn);
    }

}
