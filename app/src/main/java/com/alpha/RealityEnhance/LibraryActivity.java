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

import java.io.IOException;
import java.io.InputStream;

public class LibraryActivity extends AppCompatActivity {
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

        loadModels();

    }

    private void loadModels() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(2);

        try {
            String[] models  = getAssets().list("models");
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
                String path = "models_img/" + model_img;
                try {
                    InputStream stream = getAssets().open(path);
                    Drawable drawable = Drawable.createFromStream(stream, null);
                    button.setBackground(drawable);
                    button.setText("");
                }
                catch (IOException e){
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
