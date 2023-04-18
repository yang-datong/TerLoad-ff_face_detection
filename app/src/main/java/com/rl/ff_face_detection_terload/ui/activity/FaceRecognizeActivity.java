package com.rl.ff_face_detection_terload.ui.activity;

import android.os.Bundle;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.rl.ff_face_detection_terload.R;
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize;

public class FaceRecognizeActivity extends BaseActivity {

    private static final String TAG = "FaceRecognizeActivity";

    private FaceRecognize faceRecognize;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_face_recognize;
    }

    @Override
    public void inits() {
        TextureView mTextureView = findViewById(R.id.texture_view);
        faceRecognize = new FaceRecognize();
        faceRecognize.onCreate(mTextureView, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        faceRecognize.onResume();
    }

    @Override
    protected void onPause() {
        faceRecognize.onPause();
        super.onPause();
    }
}
