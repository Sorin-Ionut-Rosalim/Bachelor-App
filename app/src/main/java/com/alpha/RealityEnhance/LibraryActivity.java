package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class LibraryActivity extends AppCompatActivity {
    private File modelsDir;
    private File modelsImgDir;
    private File modelsTutorialDir;

    @Override
    protected void onResume() {
        super.onResume();
        loadModels();
    }

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

        backButton.setOnClickListener(view -> finish());
        this.modelsDir = new File(getFilesDir(), "models");
        this.modelsImgDir = new File(getFilesDir(), "models_img");
        this.modelsTutorialDir = new File(getFilesDir(), "models_tutorial");

        loadModels();
    }

    private void loadModels() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(2);
        File[] fileList = modelsDir.listFiles();
        int numRows = (int) Math.ceil((double) Objects.requireNonNull(fileList).length / 2);
        gridLayout.setRowCount(numRows);

        int i = 0;
        for (File file : fileList) {
            Button button = new Button(this);
            button.setOnClickListener(view -> {
                MainActivity.setSelectedModel(file.getAbsolutePath());
                Toast.makeText(this, "Selected model: " + file.getName(), Toast.LENGTH_SHORT).show();
            });

            File imgFile = new File(modelsImgDir, file.getName());
            try {
                FileInputStream stream = new FileInputStream(imgFile);
                Drawable drawable = Drawable.createFromStream(stream, null);
                button.setBackground(drawable);
                button.setText("");
                stream.close();
            } catch (IOException e) {
                button.setText(file.getName());
            }

            // Set fixed size for the button
            int buttonSizeInPixels = (int) getResources().getDimension(R.dimen.button_size);
            button.setMinimumWidth(buttonSizeInPixels);
            button.setMinimumHeight(buttonSizeInPixels);
            button.setPadding(0, 0, 0, 0);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 2, 1f);
            params.rowSpec = GridLayout.spec(i / 2, 1f);
            params.setMargins(8, 4, 8, 4);
            button.setLayoutParams(params);
            gridLayout.addView(button);

            i++;
        }
    }

}