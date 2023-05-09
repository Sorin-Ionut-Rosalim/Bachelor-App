package com.alpha.RealityEnhance;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class QRActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            Toast.makeText(QRActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            String model = result.getText().replace("https://qrco.de/", "") + ".sfb";
            Log.d(TAG, "scanned: " + model);
            try {
                String[] models = getAssets().list("models");
                List<String> modelList = Arrays.asList(models);
                if (modelList.contains(model)) {
                    MainActivity.setSelectedModel("models/" + model);
                }
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}