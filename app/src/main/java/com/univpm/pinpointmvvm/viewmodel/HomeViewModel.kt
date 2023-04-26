package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.MapRepository
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.model.services.Localization
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
@SuppressLint("MissingPermission", "StaticFieldLeak")
class HomeViewModel (activity: Activity, mapFragment: SupportMapFragment) : ViewModel(){

    private lateinit var listOfUsers : List<User>
    private val mapRepository : MapRepository = MapRepository()
    private val localization: Localization = Localization(activity)
    private lateinit var map: GoogleMap

    init {
        mapFragment.getMapAsync{
            map = it
            runBlocking {
                addCurrentPositionMarker()
            }
            addUsersPositionMarker()
        }
    }

    private fun addUsersPositionMarker() {

    }

    private suspend fun addCurrentPositionMarker() {
        coroutineScope {
            val currentPosition = localization.getCurrentPosition()
            if (currentPosition != null) {
                val markerOptions = MarkerOptions()
                    .position(currentPosition)
                    .title("Current Position")
                map.addMarker(markerOptions)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18.0f))
            }
        }
    }

}
 */

class HomeViewModel() : ViewModel() {
    private val mapRepository = MapRepository()

    val users: LiveData<List<User>> = mapRepository.getAllUsers()

}