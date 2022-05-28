package com.tudor.augmentedimage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import com.tudor.internet.DownloadUtility;

import java.io.File;
import java.util.Collection;

public class AugmentedImageActivity extends AppCompatActivity{
  private ArFragment arFragment;
  private ImageView fitToScanView;
  private static final String LogsTAG = "DownloadFileFromServer";
  private DownloadUtility download;
  private  String [] listIMG;
  //Database on server
  private final static String URL = "https://augmented-images.herokuapp.com";
  private final static String LIST_IMAGE = "listImage.txt";
  private final static String LIST_IMAGE_URL = URL + "/download/?filename=" + LIST_IMAGE;
  // Augmented image and its associated center pose anchor, keyed by the augmented image in
  // the database.

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Context context = getApplicationContext();

    download = new DownloadUtility(context);
    File file = getFileStreamPath(LIST_IMAGE);
    Log.d("File>", file.getAbsolutePath());
    if (!file.exists()){
      Thread thread = new Thread(() -> {
        Log.i(LogsTAG,LIST_IMAGE + " download . . . ");
        download.downloadFile(LIST_IMAGE_URL);
        listIMG = download.getImageNames();
        Log.i(LogsTAG,LIST_IMAGE + " downloaded.");

        for (String imageName : listIMG) {
          Log.i(LogsTAG,imageName + " download . . . ");
          String URL_IMAGE = URL + "/download/?filename=" + imageName;
          download.downloadFile(URL_IMAGE);
          Log.i(LogsTAG,imageName + " downloaded.");
        }
      });
      thread.start();
    }else{
      Log.d("Download >", "Image was found!");
    }

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

    Collection<AugmentedImage> updatedAugmentedImages =
        frame.getUpdatedTrackables(AugmentedImage.class);
    for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      if (augmentedImage.getTrackingState() == TrackingState.PAUSED) {// When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
        // but not yet tracked.
        int indexAugmentedImage = augmentedImage.getIndex();
        String nameAugmentedImage = augmentedImage.getName();

        Intent secondActivity = new Intent(getApplicationContext(), LinkToConspectus.class);
        secondActivity.putExtra("index", indexAugmentedImage);
        secondActivity.putExtra("name", nameAugmentedImage);
        startActivity(secondActivity);
      }
    }
  }
}
