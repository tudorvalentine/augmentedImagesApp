package com.tudorvalentine.augmentedimages.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    private static int BUFFER_SIZE = 4096;
    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
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

                        FileOutputStream fileOS = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);

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
