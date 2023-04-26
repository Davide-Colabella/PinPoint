package com.univpm.pinpointmvvm.model.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.suspendCoroutine


@SuppressLint("MissingPermission")
class Localization(activity: Activity) {
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)
    private lateinit var location: Location

    suspend fun getCurrentPosition(): LatLng? {
        return try {
            location = fusedLocationClient.lastLocation.await()
            LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            null
        }
    }


}