package com.rl.ff_face_detection_terload.extensions;


import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DynamicDeploymentData {

    private static final String TAG = "DynamicDeploymentData";

    public static int dynamicDeploymentTrainData(String assetsZipFile, String targetDirectory, Context context) {
        InputStream inputStream;
        ZipInputStream zipInputStream;

        try {
            inputStream = context.getAssets().open(assetsZipFile);
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();

                if (zipEntry.isDirectory()) {
                    File folder = new File(targetDirectory + File.separator + entryName);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                } else {
                    FileOutputStream fileOut = new FileOutputStream(targetDirectory + File.separator + entryName);
                    BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut);

                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = zipInputStream.read(buffer)) != -1) {
                        bufferedOut.write(buffer, 0, count);
                    }

                    bufferedOut.flush();
                    bufferedOut.close();
                    fileOut.close();
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int dynamicDeploymentCascadeClassifier(int id, String workPath, Context application) {
        try {
            InputStream is = application.getResources().openRawResource(id);
            File cascadeDir = new File(workPath + "/haarcascades");
            if (!cascadeDir.exists()) {
                boolean status = cascadeDir.mkdir();
                if (status) {
                    Log.d(TAG, "Folder created");
                } else {
                    Log.e(TAG, "Failed to create folder" + cascadeDir.getAbsolutePath());
                    return -1;
                }
            }
            File cascadeFile = new File(workPath + "/haarcascades", "haarcascade_frontalface.xml");
            if (!cascadeFile.exists()) {
                FileOutputStream os = new FileOutputStream(cascadeFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                is.close();
                os.close();
                Log.d(TAG, "CascadeFile write " + cascadeFile.getAbsolutePath());
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "loadCascadeClassifier: error -> ", e);
        }
        return -1;
    }
}
