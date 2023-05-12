package com.univpm.pinpointmvvm.model.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.model.repo.UserRepository
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class Localization(private val activity: FragmentActivity) : LocationListener {
    private var userRepository = UserRepository()
    private val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: ((Location?) -> Unit)? = null

    suspend fun getLastLocation(): LatLng {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            var location = Location("")
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
            } else {
                location = fusedLocationClient.lastLocation.await()
            }
            setCurrentUserPosition(location)
            LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            LatLng(0.0, 0.0)
        }
    }

    fun startUpdates(listener: (Location?) -> Unit) {
        this.listener = listener
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
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