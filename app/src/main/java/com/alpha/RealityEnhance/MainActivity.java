package com.alpha.RealityEnhance;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private Button burgerButton;

    private ImageView menuButton;
    private LinearLayout hiddenButtons;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        arFragment = new ArFragment();
        QRActivity qrScannerFragment = new QRActivity();

        menuButton = findViewById(R.id.menu_icon);

        menuButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                startActivity(intent);
        });

        burgerButton = findViewById(R.id.burgerButton);
        Button qrButton = findViewById(R.id.qrButton);
        Button libraryButton = findViewById(R.id.libraryButton);
        hiddenButtons = findViewById(R.id.button_holder);

        burgerButton.setOnClickListener(view -> {
            if (hiddenButtons.getVisibility() == View.GONE) {
                hiddenButtons.setVisibility(View.VISIBLE);
                burgerButton.setText("Hide");
            }
            else
            {
                hiddenButtons.setVisibility(View.GONE);
                burgerButton.setText("Show");
            }
        });

        libraryButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            startActivity(intent);
        });

        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, QRActivity.class);
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

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode node = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(modelRenderable);

        arFragment.getArSceneView().getScene().addChild(node);
        transformableNode.select();
    }



}