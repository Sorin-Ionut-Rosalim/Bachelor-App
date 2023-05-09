package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

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
                ImageButton button = new ImageButton(this);
                int finalI = i;
                button.setOnClickListener(v -> {
                    // Change the selected model
                    MainActivity.setSelectedModel("models/"+models[finalI]);
                });

                // Set the button's image
                String model_img = models[i].replace(".sfb", ".jpg");
                InputStream stream = getAssets().open("models_img/" + model_img);
                Drawable drawable = Drawable.createFromStream(stream, null);
                button.setImageDrawable(drawable);

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
