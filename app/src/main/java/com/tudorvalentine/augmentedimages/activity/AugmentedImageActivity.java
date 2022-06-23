package com.tudorvalentine.augmentedimages.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import com.tudorvalentine.augmentedimages.R;

import java.util.Collection;
public class AugmentedImageActivity extends AppCompatActivity{
  private static final String TAG = AugmentedImageActivity.class.getSimpleName();
  private ArFragment arFragment;
  private ImageView fitToScanView;
  // Augmented image and its associated center pose anchor, keyed by the augmented image in
  // the database.

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    fitToScanView = findViewById(R.id.image_view_fit_to_scan);
    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

  }

  @Override
  protected void onResume() {
    super.onResume();
      fitToScanView.setVisibility(View.VISIBLE);
  }

  /**
   * Registered with the Sceneform Scene object, this method is called at the start of each frame.
   *
   * @param frameTime - time since last frame.
   */
  private void onUpdateFrame(FrameTime frameTime) {
    Frame frame = arFragment.getArSceneView().getArFrame();

    // If there is no frame, just return.
    if (frame == null) {
      return;
    }

    Collection<AugmentedImage> updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

    for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      if (augmentedImage.getTrackingState() == TrackingState.PAUSED) {// When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
        // but not yet tracked.
        int indexAugmentedImage = augmentedImage.getIndex();
        String nameAugmentedImage = augmentedImage.getName();

        Intent secondActivity = new Intent(getApplicationContext(), LinkToConspectActivity.class);
        secondActivity.putExtra("index", indexAugmentedImage);
        secondActivity.putExtra("name", nameAugmentedImage);
        startActivity(secondActivity);
      }
    }
  }
}
