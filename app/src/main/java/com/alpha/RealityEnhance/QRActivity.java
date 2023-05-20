package com.alpha.RealityEnhance;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class QRActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                    String modelId = result.getText().replace("https://qrco.de/", "") + ".sfb";
                    Log.d(TAG, "scanned: " + modelId);
                }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private void downloadModel(String modelId) {
        // Download the file from the website using OkHttp
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://upload.wikimedia.org/wikipedia/commons/0/0b/Penguins_collage.png")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Save the downloaded file to the assets directory
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        InputStream input = responseBody.byteStream();
                        StringBuilder responseText = new StringBuilder();
                        byte[] data = new byte[1024];
                        int bytesRead;
                        Log.d("MODELS", "Before reading body");
                        while ((bytesRead = input.read(data)) != -1) {
                            String chunk = new String(data, 0, bytesRead, StandardCharsets.UTF_8);
                            responseText.append(chunk);
                        }
                        input.close();
                        Log.d("MODELS", "Done reading body");

                        String fileName =  modelId + ".png";

                        File internalStorageDir = getFilesDir();
                        for (File f : Objects.requireNonNull(internalStorageDir.listFiles())) {
                            Log.d("MODELS", f.getAbsolutePath());
                        }
                        File file = new File(internalStorageDir, fileName);

                        try {
                            // Create a FileWriter to write to the file
                            FileWriter writer = new FileWriter(file);

                            // Write the contents to the file
                            writer.write(responseText.toString());

                            // Close the writer to release resources
                            writer.close();

                            // File has been successfully written
                            // You can now access the file using its file path: file.getAbsolutePath()
                        } catch (IOException e) {
                            // Handle any errors that occurred during file writing
                            e.printStackTrace();
                        }


                        // Set the selected model to the downloaded file
//                        MainActivity.setSelectedModel("models/" + modelId);
                        for (File f : Objects.requireNonNull(internalStorageDir.listFiles())) {
                            Log.d("MODELS", f.getName());
                        }
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
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