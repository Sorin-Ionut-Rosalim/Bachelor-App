package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private final String TAG = "TUTORIAL";
    private ArFragment arFragment;
    private AnchorNode currentSelectedAnchorNode = null;
    private FloatingActionButton tutorialButton;
    private int tutorialStep = 0;

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
        tutorialButton = findViewById(R.id.tutorialButton);
        tutorialButton.setVisibility(View.GONE);
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
                ModelRenderable.builder().setSource(this, Uri.parse(selectedModel)).build().thenAccept(modelRenderer -> addModelToScene(anchor, modelRenderer));
            } else {
                Toast.makeText(this, "NO MODEL SELECTED", Toast.LENGTH_SHORT).show();
            }
        }));
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

        try {
            String[] assetDirectories = getAssets().list("");
            if (assetDirectories != null) {
                for (String assetDirectory : assetDirectories) {
                    if (assetDirectory.endsWith("_Tutorial")) {
                        moveAssetDirectoryToInternalStorage(assetDirectory);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveAssetDirectoryToInternalStorage(String assetDirectoryName) {
        try {
            // Get the list of files in the asset directory
            String[] fileList = getAssets().list(assetDirectoryName);

            // Create a directory in the internal storage
            File internalDirectory = new File(getFilesDir(), assetDirectoryName);
            if (!internalDirectory.exists()) {
                if (!internalDirectory.mkdirs()) {
                    Log.d(TAG, "moveAssetDirectoryToInternalStorage: FAILED TO CREATE DIRECTORY");
                    // Directory creation failed
                    return;
                }
            }
            Log.d(TAG, "moveAssetDirectoryToInternalStorage: " + fileList.length);

            // Iterate through the files in the asset directory
            for (String fileName : fileList) {
                // Open the asset file for reading
                Log.d(TAG, String.format("moveAssetDirectoryToInternalStorage files in the directory: %s", fileName));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addModelToScene(Anchor anchor, ModelRenderable model) {
        // Create the anchor node
        AnchorNode anchorNode = new AnchorNode(anchor);

        // Create the transformable node
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
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

        CheckTutorialButton();

        // Select the renderer node
        transformableNode.select();

        transformableNode.setOnTapListener((hitTestResult, motionEvent) -> currentSelectedAnchorNode = anchorNode);

        tutorialButton.setOnClickListener(view -> {
            tutorialStep += 1;

            Log.d(TAG, String.valueOf(tutorialStep));
            String modelTutorialStep;
            String[] parts = selectedModel.split("/");
            Log.d(TAG, String.format("%s", selectedModel));
            if (parts.length > 0) {
                String tutorialDir = parts[parts.length - 1] + "_Tutorial";
                if (TutorialComplete(model, transformableNode, tutorialDir)) return;

                StringBuilder modelTutorialDirBuilder = new StringBuilder();

                for (int i = 0; i < parts.length - 2; i++) {
                    modelTutorialDirBuilder.append(parts[i]).append("/");
                }
                modelTutorialDirBuilder.append(tutorialDir).append("/").append(tutorialStep);
                modelTutorialStep = modelTutorialDirBuilder.toString();
                Log.d(TAG, String.format("modelTutorialStep = %s", modelTutorialStep));

                Toast.makeText(this, "STEP" + tutorialStep, Toast.LENGTH_SHORT).show();
                // Load the model renderable from the path and set it to the transformable node
                ModelRenderable.builder()
                        .setSource(this, Uri.parse(modelTutorialStep))
                        .build()
                        .thenAccept(transformableNode::setRenderable)
                        .exceptionally(
                                throwable -> {
                                    Log.e(TAG, "Unable to load Renderable.", throwable);
                                    return null;
                                });
            }
            transformableNode.select();
        });
    }

    private boolean TutorialComplete(ModelRenderable model, TransformableNode transformableNode, String tutorialDir) {
        try {
            String[] fileList = getAssets().list(tutorialDir);
            if (tutorialStep > fileList.length) {
                tutorialStep = 0;
                Toast.makeText(this, "Tutorial Complete!", Toast.LENGTH_SHORT).show();
                transformableNode.setRenderable(model);
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void CheckTutorialButton() {
        if (checkForTutorial(selectedModel)) {
            tutorialButton.setVisibility(View.VISIBLE);
        } else {
            tutorialButton.setVisibility(View.GONE);
        }
    }

    private void removeAnchorNode(AnchorNode nodeToRemove) {
        //Remove an anchor node
        if (nodeToRemove != null) {
            arFragment.getArSceneView().getScene().removeChild(nodeToRemove);
            Objects.requireNonNull(nodeToRemove.getAnchor()).detach();
            nodeToRemove.setParent(null);
            Toast.makeText(MainActivity.this, "Test Delete - markAnchorNode removed", Toast.LENGTH_SHORT).show();
            tutorialStep = 0;
            tutorialButton.setVisibility(View.GONE);
            Log.d(TAG, String.format("removeAnchorNode: %d", tutorialStep));
        } else {
            Toast.makeText(MainActivity.this, "Delete - no node selected! Touch a node to select it.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkForTutorial(String selectedModel) {
        String[] parts = selectedModel.split("/");
        if (parts.length > 0) {
            String modelName = parts[parts.length - 1] + "_Tutorial";
            Log.d(TAG, String.format("%s", modelName));
            File tutorialDir = new File(getFilesDir(), modelName);
            Log.d(TAG, String.format("%s", tutorialDir.getAbsolutePath()));
            return tutorialDir.exists() && tutorialDir.isDirectory();
        }
        return false;
    }
}
