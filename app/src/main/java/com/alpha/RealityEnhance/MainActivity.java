package com.alpha.RealityEnhance;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static String selectedModel = null;
    private ArFragment arFragment;
    private AnchorNode currentSelectedAnchorNode = null;

    public static void setSelectedModel(String modelPath) {
        selectedModel = modelPath;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        arFragment = new ArFragment();

        ImageView libraryButton = findViewById(R.id.libraryButton);
        ImageView qrButton = findViewById(R.id.qrButton);
        FloatingActionButton deleteButton = findViewById(R.id.deleteButton);
        tryToMoveAssets();

        libraryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, QRActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(view -> {
            if (currentSelectedAnchorNode != null) {
                removeAnchorNode(currentSelectedAnchorNode);
                currentSelectedAnchorNode = null;
            } else {
                Toast.makeText(MainActivity.this, "Delete - no node selected! Touch a node to select it.", Toast.LENGTH_SHORT).show();
            }
        });


        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        assert arFragment != null;


        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            Log.d(TAG, "CLICKED ON AN EMPTY SPACE " + hitResult);
            if (selectedModel != null) {
                ModelRenderable.builder()
                        .setSource(this, Uri.parse(selectedModel))
                        .build()
                        .thenAccept(modelRenderer -> addModelToScene(anchor, modelRenderer));
            } else {
                Toast.makeText(this, "NO MODEL SELECTED", Toast.LENGTH_SHORT).show();
            }

        }));

    }

    private void moveAssetDirectoryToInternalStorage(String assetDirectoryName) {
        try {
            // Get the list of files in the asset directory
            String[] fileList = getAssets().list(assetDirectoryName);

            // Create a directory in the internal storage
            File internalDirectory = new File(getFilesDir(), assetDirectoryName);
            if (!internalDirectory.exists()) {
                if (!internalDirectory.mkdirs()) {
                    // Directory creation failed
                    return;
                }
            }

            // Iterate through the files in the asset directory
            for (String fileName : fileList) {
                // Open the asset file for reading
                InputStream inputStream = getAssets().open(assetDirectoryName + File.separator + fileName);

                // Create a new file in the internal storage directory
                File internalFile = new File(internalDirectory, fileName);
                FileOutputStream outputStream = new FileOutputStream(internalFile);

                // Read the data from the asset file and write it to the new file in the internal storage
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                // Close the streams
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }

            // The directory and its contents have been moved to the internal storage
            // You can now access the files in the internalDirectory
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryToMoveAssets() {
        File internalStorageDir = getFilesDir();

        File modelsDir = new File(internalStorageDir, "models");
        File modelImgsDir = new File(internalStorageDir, "models_img");
        if (modelsDir.exists() && modelsDir.isDirectory() && modelImgsDir.exists() && modelImgsDir.isDirectory()) {
            return;
        }
        moveAssetDirectoryToInternalStorage("models");
        moveAssetDirectoryToInternalStorage("models_img");

    }

    private void addModelToScene(Anchor anchor, ModelRenderable model) {
        // Create the anchor node
        AnchorNode anchorNode = new AnchorNode(anchor);

        // Create the transformable node
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(model);

        // Add the node to the scene
        arFragment.getArSceneView().getScene().addChild(anchorNode);

        // Set the min and max scales of the ScaleController.
        transformableNode.getScaleController().setMinScale(0.4f);
        transformableNode.getScaleController().setMaxScale(1.2f);

        // Set the local scale of the node BEFORE setting its parent
        transformableNode.setLocalScale(new Vector3(0.55f, 0.55f, 0.55f));

        transformableNode.setParent(anchorNode);

        currentSelectedAnchorNode = anchorNode;
        Log.d(TAG, "CLICKED CREATED OBJECT WITH MODEL " + model.getId());
        // Select the renderer node
        transformableNode.select();

        transformableNode.setOnTapListener((hitTestResult, motionEvent) -> {
            Log.d(TAG, "CLICKED ON OBJECT " + model.getId());
            currentSelectedAnchorNode = anchorNode;
            transformableNode.select();
        });
    }

    private void removeAnchorNode(AnchorNode nodeToRemove) {
        //Remove an anchor node
        if (nodeToRemove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToRemove);
            Objects.requireNonNull(nodeToRemove.getAnchor()).detach();
            nodeToRemove.setParent(null);
            Toast.makeText(MainActivity.this, "Test Delete - markAnchorNode removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Delete - no node selected! Touch a node to select it.", Toast.LENGTH_SHORT).show();
        }
    }
}