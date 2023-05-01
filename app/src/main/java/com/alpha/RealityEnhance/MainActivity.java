package com.alpha.RealityEnhance;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private AnchorNode currentSelectedAnchorNode = null;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        arFragment = new ArFragment();

        ImageView libraryButton = findViewById(R.id.libraryButton);
        ImageView qrButton = findViewById(R.id.qrButton);
        FloatingActionButton deleteButton = findViewById(R.id.deleteButton);

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
            Toast.makeText(this, "MISS TEST", Toast.LENGTH_SHORT).show();
            ModelRenderable.builder()
                    .setSource(this, Uri.parse("Chair.sfb"))
                    .build()
                    .thenAccept(modelRenderer -> addModelToScene(anchor, modelRenderer));
        }));

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
        Log.d(TAG, "CLICKED CREATED OBJECT WITH MODEL " + model.getId());
        // Select the renderer node
        transformableNode.select();

        transformableNode.setOnTapListener((hitTestResult, motionEvent) -> {
            Log.d(TAG, "CLICKED ON OBJECT " + model.getId());
            currentSelectedAnchorNode = anchorNode;
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