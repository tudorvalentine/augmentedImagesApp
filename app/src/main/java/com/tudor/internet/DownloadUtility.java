package com.tudor.internet;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DownloadUtility {
    private static String LogsTAG = "DownloadUtility";
    private static int BUFFER_SIZE = 4096;
    private static final String LIST_IMAGE = "listImage.txt";
    private Context cont;
    private String listStorage;

    public DownloadUtility(Context context){
        cont = context;
    }

    public void downloadFile(String fileURL){

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(fileURL).openStream())) {
            URL url = new URL(fileURL);
            String [] fileName = url.getFile().split("=");
            FileOutputStream fileOS = cont.getApplicationContext().openFileOutput(fileName[1], Context.MODE_PRIVATE);
            Log.d(LogsTAG,"FileName > " + fileName[1]);
            byte [] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
        } catch (IOException e) {
            Log.e(LogsTAG,"Error Download  " + e.getMessage());
        }

//        URL url = new URL(fileURL);
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        int responseCode = httpConn.getResponseCode();
//
//        // always check HTTP response code first
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            String fileName = "";
//            String disposition = httpConn.getHeaderField("Content-Disposition");
//            String contentType = httpConn.getContentType();
//            int contentLength = httpConn.getContentLength();
//
//            if (disposition != null) {
//                // extracts file name from header field
//                int index = disposition.indexOf("filename=");
//                if (index > 0) {
//                    fileName = disposition.substring(index + 10,
//                            disposition.length() - 1);
//                }
//            } else {
//                // extracts file name from URL
//                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
//            }
//            Log.d(LogsTAG,"Content-Type = " + contentType);
//            Log.d(LogsTAG,"Content-Disposition = " + disposition);
//            Log.d(LogsTAG,"Content-Length = " + contentLength);
//            Log.d(LogsTAG,"fileName = " + fileName);
//
//            // opens input stream from the HTTP connection
//            InputStream inputStream = httpConn.getInputStream();
//            // opens an output stream to save into file
//            FileOutputStream outputStream = cont.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
//            int bytesRead = -1;
//            byte[] buffer = new byte[BUFFER_SIZE];
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//
//            outputStream.close();
//            inputStream.close();
//
//            Log.d(LogsTAG,"File downloaded");
//        } else {
//            Log.e(LogsTAG,"No file to download. Server replied HTTP code: " + responseCode);
//        }
//        httpConn.disconnect();
    }

    public String[] getImageNames(){
        try {
            FileInputStream inputStream = cont.openFileInput(LIST_IMAGE);
            byte[] readBytes = new byte[inputStream.available()];
            inputStream.read(readBytes);
            listStorage = new String(readBytes);
            inputStream.close();
        }catch (IOException e){
            Log.e("FILE EXCEPTION >> ", e.getMessage());
        }
        return listStorage.split(" ");
    }
}
