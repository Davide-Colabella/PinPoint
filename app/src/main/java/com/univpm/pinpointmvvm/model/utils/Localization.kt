package com.univpm.pinpointmvvm.model.utils

import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.model.repo.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Localization(private val activity: Activity) : LocationListener {

    companion object {
        const val LATITUDE_DEFAULT = 41.9027835
        const val LONGITUDE_DEFAULT = 12.4963655
    }

    //User Repository
    private val userRepository = UserRepository()

    //Permissions Manager & Shared Preferences
    private val permissionsManager = PermissionsManager(activity)
    private val sharedPreferences = SharedPreferences(activity)

    //Location
    private var location = Location("")
    private val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: ((Location?) -> Unit)? = null


    suspend fun getLastLocation(): LatLng {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

            if (permissionsManager.checkLocationPermission()) {
                location = fusedLocationClient.lastLocation.await()
                setCurrentUserPosition(location)
                LatLng(location.latitude, location.longitude)
            }else{
                location = Location("")
                location.longitude = LONGITUDE_DEFAULT
                location.latitude = LATITUDE_DEFAULT
                setCurrentUserPosition(location)
                LatLng(LATITUDE_DEFAULT, LONGITUDE_DEFAULT)
            }


        } catch (e: Exception) {
            LatLng(LATITUDE_DEFAULT, LONGITUDE_DEFAULT)
        }
    }

    suspend fun startUpdates(listener: (Location?) -> Unit) {
        this.listener = listener

        if (permissionsManager.checkLocationPermission()) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000L, 10f, this
            )
        }
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