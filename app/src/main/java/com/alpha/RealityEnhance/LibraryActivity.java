package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("tesssst", "onResume: ");
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
        loadModels();

    }

    private void loadModels() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(2);
        for (File f : Objects.requireNonNull(modelsImgDir.listFiles())) {
            Log.d("IMAGES", f.getAbsolutePath());
        }
        File[] fileList = modelsDir.listFiles();
        int numRows = (int) Math.ceil((double) Objects.requireNonNull(fileList).length / 2);
        gridLayout.setRowCount(numRows);

        // Loop through each file in the "models" folder
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
