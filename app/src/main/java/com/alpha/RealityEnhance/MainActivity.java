package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        arFragment = new ArFragment();

        ImageView libraryButton = findViewById(R.id.libraryButton);
        ImageView qrButton = findViewById(R.id.qrButton);

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



        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        assert arFragment != null;
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("Chair.sfb"))
                    .build()
                    .thenAccept(modelRenderer -> addModelToScene(anchor, modelRenderer));
        }));

    }

    private void addModelToScene(Anchor anchor, ModelRenderable model) {
        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(model);

        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }

//    TODO
//    private void removeModelFromScene() {
//
//    }
}