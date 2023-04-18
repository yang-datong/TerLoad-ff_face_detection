package com.example.myapplication.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.hyphenate.chat.EMClient
import org.jetbrains.anko.startActivity

class DefaultActivity : AppCompatActivity() {
    private var permissionsPass = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        val bDetection = findViewById<Button>(R.id.bt_detection)
        val bAdminLogin = findViewById<Button>(R.id.bt_admin_login)

//        Button bUpload = findViewById(R.id.upload);


        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
//                || ContextCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, PERMISSIONS_STORAGE[2]) != PackageManager.PERMISSION_GRANTED) {
            permissionsPass = false
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE, REQUEST_CAMERA_PERMISSION)
        }
        bDetection.setOnClickListener { v: View? ->
            if (permissionsPass)
                startActivity<FaceRecognizeActivity>()
        }
        bAdminLogin.setOnClickListener { v: View? ->
            if (permissionsPass) {
                if (isLoggedIn()) onLoggedIn() else onNotLoggedIn()
            }
        }
        //        bUpload.setOnClickListener(v -> {
//            if (permissionsPass)
//                startActivity(new Intent(this, UploadFaceActivity.class));
//        });
    }

    private fun isLoggedIn() = EMClient.getInstance().isConnected && EMClient.getInstance().isLoggedInBefore

    private fun onNotLoggedIn() {
        startActivity<LoginActivity>()
        finish()
    }

    private fun onLoggedIn() {
        startActivity<MainActivity>()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.length == 3
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsPass = true
                // Camera permission has been granted, start the camera preview
            } else {
                // Camera permission has been denied, show an error message
                Toast.makeText(this, "Camera permission was not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 0x111
        private val PERMISSIONS_STORAGE = arrayOf( //            Manifest.permission.READ_EXTERNAL_STORAGE,
                //            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        )
    }
}