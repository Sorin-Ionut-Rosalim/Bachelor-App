package com.alpha.RealityEnhance;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class QRActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private File modelsDir;
    private File modelsImgDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);
        this.modelsDir = new File(getFilesDir(), "models");
        this.modelsImgDir = new File(getFilesDir(), "models_img");
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            String modelId = result.getText();
            Log.d("MODELS", "scanned: " + modelId);
            for (File f : Objects.requireNonNull(modelsDir.listFiles())) {
                if (f.getName().equals(modelId)) {
                    Log.d("MODELS", "Found " + modelId + " locally. Selecting it.");
                    MainActivity.setSelectedModel(f.getAbsolutePath());
                    finish();
                    return;
                }
            }
            Log.d("MODELS", "Not found " + modelId + " locally. Downloading it.");

            downloadModel(modelId);
        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private void downloadModel(String modelId) {
        // Download the file from the website using OkHttp
        OkHttpClient client = new OkHttpClient();
        Request modelRequest = new Request.Builder()
                .url("https://storage.googleapis.com/reality-enhance-bucket/models/" + modelId)
                .build();
        Request modelImgRequest = new Request.Builder()
                .url("https://storage.googleapis.com/reality-enhance-bucket/models_img/" + modelId)
                .build();

        client.newCall(modelRequest).enqueue(new RequestCallback(modelId, modelsDir));
        client.newCall(modelImgRequest).enqueue(new RequestCallback(modelId, modelsImgDir));
    }

    private class RequestCallback implements Callback {
        private final String modelId;
        private final File parentDir;

        public RequestCallback(String modelId, File parentDir) {
            this.modelId = modelId;
            this.parentDir = parentDir;
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.d("MODELS", "Request for " + modelId + " in " + parentDir.getAbsolutePath() + " FAIL: " + response.code());
            }
            if (response.isSuccessful()) {
                Log.d("MODELS", "Request for " + modelId + " in " + parentDir.getAbsolutePath() + " SUCCESS: " + response.code());

                // Save the downloaded file to the assets directory
                File internalStorageDir = getFilesDir();
                for (File f : Objects.requireNonNull(internalStorageDir.listFiles())) {
                    Log.d("MODELS", f.getAbsolutePath());
                }
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    File file = new File(parentDir, modelId);
                    InputStream inputStream = responseBody.byteStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[8192]; // Choose an appropriate buffer size
                    int bytesRead;
                    float contentSize = Float.parseFloat(Objects.requireNonNull(response.header("Content-Length")));
                    int total = 0;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                        total += bytesRead;
                        Log.d("MODELS", "WROTE to " + file.getAbsolutePath() + " " + ((total*100.0f)/contentSize +"%"));

                    }

                    fileOutputStream.close();
                    inputStream.close();
                    Log.d("MODELS", "Done writing to file");



                    for (File f : Objects.requireNonNull(internalStorageDir.listFiles())) {
                        Log.d("MODELS", f.getName());
                    }
                    if (parentDir.equals(modelsDir)) {
                        MainActivity.setSelectedModel(file.getAbsolutePath());
                    }
                    finish();
                }
            }
        }
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