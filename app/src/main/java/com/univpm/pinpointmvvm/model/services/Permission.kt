package com.univpm.pinpointmvvm.model.services

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission(private val activity: AppCompatActivity) {

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        const val PERMISSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val PERMISSION_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val REQUEST_CODE = 123
    }

    fun checkPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(activity, PERMISSION_CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        val fineLocation = ContextCompat.checkSelfPermission(activity, PERMISSION_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val coarseLocation =
            ContextCompat.checkSelfPermission(activity, PERMISSION_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        return camera && fineLocation && coarseLocation
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                PERMISSION_CAMERA,
                PERMISSION_FINE_LOCATION,
                PERMISSION_COARSE_LOCATION
            ), REQUEST_CODE
        )
    }
}
