package com.univpm.pinpointmvvm.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.repo.UserRepository
import kotlinx.coroutines.tasks.await

/**
 * Classe che rappresenta la localizzazione dell'utente
 */
class Localization(private val activity: Activity) : LocationListener {

    companion object {
        const val LATITUDE_DEFAULT = 41.9027835
        const val LONGITUDE_DEFAULT = 12.4963655
    }

    //User Repository
    private val userRepository = UserRepository()

    //Permissions Manager & Shared Preferences
    private val permissionsManager = PermissionsManager(activity)

    //Location
    private var location = Location("")
    private val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: ((Location?) -> Unit)? = null


    @SuppressLint("MissingPermission")
    /**
     * Funzione che ritorna la posizione attuale dell'utente
     * @return LatLng
     * @suppress "MissingPermission"
     */
    suspend fun getLastLocation(): LatLng {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        return try {
            location = fusedLocationClient.lastLocation.await()
            setCurrentUserPosition(location)
            LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            LatLng(LATITUDE_DEFAULT, LONGITUDE_DEFAULT)
        }
    }

    @SuppressLint("MissingPermission")
            /**
             * Funzione che inizia a monitorare la posizione dell'utente
             * @param listener
             * @suppress "MissingPermission"
             */
    fun startUpdates(listener: (Location?) -> Unit) {
        this.listener = listener

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 1000L, 10f, this
        )

    }

    /**
     * Funzione che setta la posizione attuale dell'utente
     * @param location posizione attuale dell'utente
     */
    private fun setCurrentUserPosition(location: Location) {
        userRepository.updateProfile(
            latitude = location.latitude.toString(),
            longitude = location.longitude.toString()
        )
    }

    /**
     * Funzione che monitora il cambiamento della posizione dell'utente
     * @param location posizione attuale dell'utente
     */
    override fun onLocationChanged(location: Location) {
        listener?.invoke(location)
        setCurrentUserPosition(location)
    }


}