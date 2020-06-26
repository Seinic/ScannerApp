package com.test.scannerapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.test.scannerapp.R
import com.test.scannerapp.ui.scanner.ScannerFragment
import com.test.scannerapp.util.REQUEST_CAMERA_PERMISSION

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            checkIfCameraPermissionGranted()
        }
    }

    private fun checkIfCameraPermissionGranted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            startScannerFragment()
        }
    }

    private fun startScannerFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ScannerFragment()).commit()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScannerFragment()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.camera_permission_denied_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}