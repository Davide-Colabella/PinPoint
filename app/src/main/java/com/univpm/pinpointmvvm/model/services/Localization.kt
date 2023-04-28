package com.univpm.pinpointmvvm.model.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.model.repo.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.suspendCoroutine


@SuppressLint("MissingPermission")
class Localization(private val activity: Activity) : LocationListener {
    private var userRepository = UserRepository()
    private val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: ((Location?) -> Unit)? = null

    suspend fun getLastLocation(): LatLng? {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            val location = fusedLocationClient.lastLocation.await()
            setCurrentUserPosition(location)
            LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            null
        }
    }

    fun startUpdates(listener: (Location?) -> Unit) {
        this.listener = listener
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000L, 10f, this
        )
    }

    fun stopUpdates() {
        locationManager.removeUpdates(this)
    }


    private fun setCurrentUserPosition(location: Location) {
        userRepository.updateProfile(
            latitude = location.latitude.toString(), longitude = location.longitude.toString()
        )
    }

    override fun onLocationChanged(location: Location) {
        listener?.invoke(location)
        setCurrentUserPosition(location)
    }


}