package com.alpha.RealityEnhance;

import android.os.Bundle;
import android.widget.Toast;

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
//    private File modelsTutorialDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);
        this.modelsDir = new File(getFilesDir(), "models");
        this.modelsImgDir = new File(getFilesDir(), "models_img");
//        this.modelsTutorialDir = new File(getFilesDir(), "models_tutorial");
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            String modelId = result.getText();
            for (File f : Objects.requireNonNull(modelsDir.listFiles())) {
                // IF the model is available, load it
                if (f.getName().equals(modelId)) {
                    MainActivity.setSelectedModel(f.getAbsolutePath());
                    finish();
                    return;
                }
            }
            downloadModel(modelId);
        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private void downloadModel(String modelId) {
        // Download a model using OkHttp
        OkHttpClient client = new OkHttpClient();
        Request modelRequest = new Request.Builder()
                .url("https://storage.googleapis.com/reality-enhance-bucket/models/" + modelId)
                .build();
        Request modelImgRequest = new Request.Builder()
                .url("https://storage.googleapis.com/reality-enhance-bucket/models_img/" + modelId)
                .build();
//        Request modelTutorialRequest = new Request.Builder()
//                .url("https://storage.googleapis.com/reality-enhance-bucket/models_tutorial/" + modelId)
//                .build();

        client.newCall(modelRequest).enqueue(new RequestCallback(modelId, modelsDir));
        client.newCall(modelImgRequest).enqueue(new RequestCallback(modelId, modelsImgDir));
//        client.newCall(modelTutorialRequest).enqueue(new RequestCallback(modelId, modelsTutorialDir));
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
            if (!response.isSuccessful()) { //Handle fail response
                String error = String.format("Model with ID had a problem: %s", modelId);
                runOnUiThread(() -> {
                    Toast.makeText(QRActivity.this, error, Toast.LENGTH_SHORT).show();
                    mCodeScanner.startPreview();
                });
            }
            if (response.isSuccessful()) { //Handle success response
                // Save the downloaded file to the assets directory
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    File file = new File(parentDir, modelId);
                    InputStream inputStream = responseBody.byteStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[8192]; // Buffer size
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    fileOutputStream.close();
                    inputStream.close();
                    if (parentDir.equals(modelsDir)) {
                        MainActivity.setSelectedModel(file.getAbsolutePath());
                    }
                    finish();
                }
            }
        }
    }
}
