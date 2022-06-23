package com.tudorvalentine.augmentedimages.helpers;

import android.content.Context;
import android.util.Log;

import com.tudorvalentine.augmentedimages.app.AppConfig;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DownloadUtility {
    private static final String TAG = DownloadUtility.class.getSimpleName();
    private static int BUFFER_SIZE = 4096;
    private Context cont;

    private SQLiteHandler db;

    public DownloadUtility(Context context){
        cont = context;
    }

    public void downloadFile(String fileName){
        String fileURL = AppConfig.URL_IMAGES + "/" + fileName;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try (BufferedInputStream inputStream = new BufferedInputStream(new URL(fileURL).openStream())) {
                        URL url = new URL(fileURL);

                        Log.d(TAG, "Download file > " + url.getFile());

                        FileOutputStream fileOS = cont.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);

                        byte [] data = new byte[BUFFER_SIZE];
                        int byteContent;
                        while ((byteContent = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                            fileOS.write(data, 0, byteContent);
                        }
                    } catch (IOException e) {
                        Log.e(TAG,"Error Download  " + e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
