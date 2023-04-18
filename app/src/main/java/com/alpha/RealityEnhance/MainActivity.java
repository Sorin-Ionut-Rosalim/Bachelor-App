package com.alpha.RealityEnhance;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
//import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

//import java.util.ArrayList;
//import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArFragment arFragment;
//    private AnchorNode anchorNode;
//    private List<AnchorNode> anchorNodeList = new ArrayList<>();

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
        // Create the anchor node
        AnchorNode node = new AnchorNode(anchor);

        // Create the transformable node
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(node);
        transformableNode.setRenderable(model);

        // setOnTapListener is a method used in ARCore for detecting a tap gesture on a detected plane or a feature point.
        // It is specific to ARCore and is used to interact with the AR scene.
        transformableNode.setOnTapListener((hitTestResult, motionEvent) -> {

        });


        // Add the node to the scene
        arFragment.getArSceneView().getScene().addChild(node);
        // Select the renderable node
        transformableNode.select();
    }

//    private void removeAnchorNode(AnchorNode nodeToRemove) {
//        //Remove an anchor node
//        if (nodeToRemove != null) {
//            arFragment.getArSceneView().getScene().removeChild(nodeToRemove);
//            anchorNodeList.remove(nodeToRemove);
//            nodeToRemove.getAnchor().detach();
//            nodeToRemove.setParent(null);
//            nodeToRemove = null;
//        } else {
//            //Handle error case here
//        }

}