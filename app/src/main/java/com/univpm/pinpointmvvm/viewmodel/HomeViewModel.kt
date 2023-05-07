package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.CropTransformation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.model.services.Localization
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment
import kotlinx.coroutines.launch

/*
private lateinit var mapBounds: LatLngBounds
mapBounds = LatLngBounds(
                LatLng(position.latitude, position.longitude),  // SW bounds
                LatLng(position.latitude, position.longitude) // NE bounds
            )
map.setLatLngBoundsForCameraTarget(mapBounds)
 */


@SuppressLint("MissingPermission", "StaticFieldLeak", "PotentialBehaviorOverride")
class HomeViewModel(
    private val supportMapFragment: SupportMapFragment,
    private val fragment: FragmentActivity,
) : ViewModel() {

    private lateinit var position: LatLng
    private lateinit var map: GoogleMap
    private val userRepository = UserRepository()
    private val localization: Localization = Localization(fragment)
    private val users: LiveData<List<User>> = userRepository.fetchAllUsersOnDatabase()

    init {
        /*
            Aspetta che si conosca la posizione.
            Crea i confini di visualizzazione della mappa.
            A mappa pronta, setup della mappa
         */
        fragment.lifecycleScope.launch {
            position = localization.getLastLocation()

            supportMapFragment.getMapAsync {
                map = it
                mapSetUi()
                mapAddMarkers()
                mapSnippetClick()
                mapLocationUpdates()
            }
        }
    }

    //aggiunta dei marker sulla mappa
    private fun mapAddMarkers() {
        val imageLoader = Coil.imageLoader(fragment)
        users.observe(fragment) { userList ->
            map.clear()
            if (map.cameraPosition.zoom > 15.0f) {
                userList.forEach { user ->
                    val position = LatLng(user.latitude!!.toDouble(), user.longitude!!.toDouble())
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(user.username)
                    )
                    marker!!.tag = user
                    val request = ImageRequest.Builder(fragment)
                        .data(user.image)
                        .transformations(
                            CircleCropTransformation(),
                            CropTransformation(CropTransformation.CropType.CENTER)
                        )
                        .size(150)
                        .target { drawable ->
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(drawable.toBitmap()))
                        }
                        .build()
                    imageLoader.enqueue(request)
                }
            }
        }
    }

    private fun mapLocationUpdates() {
        //inizio dell'aggiornamento della posizione
        localization.startUpdates { location ->
            position = LatLng(location!!.latitude, location.longitude)
            val currentZoom = map.cameraPosition.zoom
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoom))
        }
    }

    //definisce il click sullo snippet del marker
    private fun mapSnippetClick() {
        map.setOnInfoWindowClickListener { marker ->
            val user = marker.tag as User
            val bundle = Bundle().apply {
                putParcelable(Constants.USER_OBJECT_PARCEL, user)
            }
            val destinationFragment = OtherProfileFragment.newInstance().apply {
                arguments = bundle
            }
            fragment.apply {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, destinationFragment)
                    .commit()

                findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                    selectedItemId = R.id.nothing
                }
            }
        }
    }

    private fun mapSetUi() {
        map.apply {
            val cameraPosition = CameraPosition.Builder()
                .tilt(45.0f)
                .target(position)
                .zoom(18.0f)
                .build()

            moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            isMyLocationEnabled = true
            isIndoorEnabled = false
            isBuildingsEnabled = true
            uiSettings.isTiltGesturesEnabled = false
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = false
        }
    }


    override fun onCleared() {
        super.onCleared()
        localization.stopUpdates()
    }


    class Factory(
        private val mapFragment: SupportMapFragment,
        private val activity: FragmentActivity,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(mapFragment, activity) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}