package com.tudor.augmentedimage;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extend the ArFragment to customize the ARCore session configuration to include Augmented Images.
 */
public class AugmentedImageFragment extends ArFragment{
  private static final String TAG = "AugmentedImageFragment";

  // This is the name of the image in the sample database.  A copy of the image is in the assets
  // directory.  Opening this image on your computer is a good quick way to test the augmented image
  // matching.
//  private static final String DEFAULT_IMAGE_NAME = "pointer.jpg";
  // This is a pre-created database containing the sample image.
  private static final String SAMPLE_IMAGE_DATABASE = "database.imgdb";
  private static final String LIST_IMAGE = "listImage.txt";
  // Augmented image configuration and rendering.
  // Load a single image (true) or a pre-generated image database (false).
  private static final boolean USE_SINGLE_IMAGE = true;

  // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the
  // application.
  private static final double MIN_OPENGL_VERSION = 3.0;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    // Check for Sceneform being supported on this device.  This check will be integrated into
    // Sceneform eventually.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(getContext(), "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
    }

    String openGlVersionString =
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later");
      Toast.makeText(getContext(), "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);

    // Turn off the plane discovery since we're only looking for images
    getPlaneDiscoveryController().hide();
    getPlaneDiscoveryController().setInstructionView(null);
    getArSceneView().getPlaneRenderer().setEnabled(false);
    return view;
  }

  @Override
  protected Config getSessionConfiguration(Session session) {
    Config config = super.getSessionConfiguration(session);
    config.setFocusMode(Config.FocusMode.AUTO);
    if (!setupAugmentedImageDatabase(config, session)) {
      Toast.makeText(getContext(), "Could not setup augmented image database", Toast.LENGTH_SHORT).show();
    }
    return config;
  }

  private boolean setupAugmentedImageDatabase(Config config, Session session) {
    AugmentedImageDatabase augmentedImageDatabase;

//    AssetManager assetManager = getContext() != null ? getContext().getAssets() : null;
//    if (assetManager == null) {
//      Log.e(TAG, "Context is null, cannot intitialize image database.");
//      return false;
//    }

    // There are two ways to configure an AugmentedImageDatabase:
    // 1. Add Bitmap to DB directly
    // 2. Load a pre-built AugmentedImageDatabase
    // Option 2) has
    // * shorter setup time
    // * doesn't require images to be packaged in apk.
    if (USE_SINGLE_IMAGE) {
      // Obtain image list from file listImage.txt. It was downloaded from the server.
      String [] imageName;
      String listStorage = null;
      try {
        FileInputStream inputStream = getActivity().openFileInput(LIST_IMAGE);
        byte[] readBytes = new byte[inputStream.available()];
        inputStream.read(readBytes);
        listStorage = new String(readBytes);
        inputStream.close();
      }catch (IOException e){
        Log.e("FILE EXCEPTION >> ", e.getMessage());
      }
      imageName = listStorage.split(" ");

      augmentedImageDatabase = new AugmentedImageDatabase(session);
      Bitmap augmentedImageBitmap = null;
      for (String fileName : imageName) {
        augmentedImageBitmap = loadAugmentedImageBitmap(fileName);
        augmentedImageDatabase.addImage(fileName, augmentedImageBitmap);
        Log.d("ImageName>>>>>", fileName);
      }

      if (augmentedImageBitmap == null) {
        return false;
      }

      // If the physical size of the image is known, you can instead use:
      //     augmentedImageDatabase.addImage("image_name", augmentedImageBitmap, widthInMeters);
      // This will improve the initial detection speed. ARCore will still actively estimate the
      // physical size of the image as it is viewed from multiple viewpoints.
    } else {
      // This is an alternative way to initialize an AugmentedImageDatabase instance,
      // load a pre-existing augmented image database.
      try (FileInputStream is = getActivity().openFileInput(SAMPLE_IMAGE_DATABASE)) {
        augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, is);
      } catch (IOException e) {
        Log.e(TAG, "IO exception loading augmented image database.", e);
        return false;
      }
    }

    config.setAugmentedImageDatabase(augmentedImageDatabase);
    return true;
  }

  private Bitmap loadAugmentedImageBitmap(String imageName) {
    try (InputStream is = getActivity().openFileInput(imageName)) {
      return BitmapFactory.decodeStream(is);
    } catch (IOException e) {
      Log.e(TAG, "IO exception loading augmented image bitmap.", e);
    }
    return null;
  }
}
