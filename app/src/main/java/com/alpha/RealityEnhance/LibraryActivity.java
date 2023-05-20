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
        for (File f: Objects.requireNonNull(modelsImgDir.listFiles())) {
            Log.d("IMAGES", f.getAbsolutePath());
        }
        File[] fileList = modelsDir.listFiles();

        String[] models = new String[Objects.requireNonNull(fileList).length];
        for (int i = 0; i < fileList.length; i++) {
            models[i] = fileList[i].getName();
        }
        int numRows = (int) Math.ceil((double) models.length / 2);
        gridLayout.setRowCount(numRows);

        // Loop through each file in the "models" folder
        for (int i = 0; i < models.length; i++) {
            // Create a button for each model file
            Button button = new Button(this);
            int finalI = i;
            button.setOnClickListener(view -> {
                // Change the selected model
                MainActivity.setSelectedModel("models/" + models[finalI]);
                Toast.makeText(this, "Selected model: " + models[finalI], Toast.LENGTH_SHORT).show();
            });

            // Set the button's image
            String model_img = models[i].replace(".sfb", ".jpg");
//            String path = getFilesDir() + "models_img/" + model_img;
            File imgFile = new File(modelsImgDir, model_img);
            Log.d("IMAGES", imgFile.getAbsolutePath());
            try {
                FileInputStream stream = new FileInputStream(imgFile);
                Drawable drawable = Drawable.createFromStream(stream, null);
                button.setBackground(drawable);
                button.setText("");
                stream.close();
            } catch (IOException e) {
                Log.d("IMAGES", e.getMessage());
                // Create a button for each model that doesn't have a picture
                button.setText(models[i].replace(".sfb", ""));
            }

            // Set the button's layout parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 2, 1f);
            params.rowSpec = GridLayout.spec(i / 2, 1f);
            params.setMargins(8, 4, 8, 4);
            button.setLayoutParams(params);

            gridLayout.addView(button);
        }
    }

}
