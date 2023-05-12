package com.univpm.pinpointmvvm.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.CropTransformation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.model.utils.Localization
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment

/*
private lateinit var mapBounds: LatLngBounds
mapBounds = LatLngBounds(
                LatLng(position.latitude, position.longitude),  // SW bounds
                LatLng(position.latitude, position.longitude) // NE bounds
            )
map.setLatLngBoundsForCameraTarget(mapBounds)
 */


@SuppressLint("StaticFieldLeak", "PotentialBehaviorOverride")
class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val _users: MutableLiveData<List<User>> = MutableLiveData()
    val users: LiveData<List<User>> = _users


    init {
        userRepository.fetchAllUsersOnDatabase(_users)
    }

    //aggiunta dei marker sulla mappa
    fun addMarkers(
        imageLoader: ImageLoader, homeFragment: FragmentActivity, map: GoogleMap, usersList: List<User>
    ) {
        val markers = mutableListOf<Marker>()
        var loadedImages = 0
        map.clear()
        usersList.forEach { user ->
            val position = LatLng(user.latitude!!.toDouble(), user.longitude!!.toDouble())
            val marker = map.addMarker(
                MarkerOptions().position(position).title(user.username)
            )
            marker!!.isVisible = false
            marker.tag = user
            markers.add(marker)
            val request = ImageRequest.Builder(homeFragment).data(user.image).transformations(
                CircleCropTransformation(), CropTransformation(CropTransformation.CropType.CENTER)
            ).size(150).target { drawable ->
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(drawable.toBitmap()))
                loadedImages++
                if (loadedImages == usersList.size) {
                    // All images have been loaded, add the markers to the map
                    markers.forEach { mapMarker ->
                        mapMarker.isVisible = true
                    }
                }
            }.build()
            imageLoader.enqueue(request)
        }

    }

    fun mapLocationUpdates(localization: Localization, map: GoogleMap) {
        //inizio dell'aggiornamento della posizione
        localization.startUpdates { location ->
            val position = LatLng(location!!.latitude, location.longitude)
            val currentZoom = map.cameraPosition.zoom
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoom))
        }
    }

    //definisce il click sullo snippet del marker
    fun mapSnippetClick(homeFragment: FragmentActivity, map: GoogleMap) {
        map.setOnInfoWindowClickListener { marker ->
            val user = marker.tag as User
            val bundle = Bundle().apply {
                putParcelable(Constants.USER_OBJECT_PARCEL, user)
            }
            val destinationFragment = OtherProfileFragment.newInstance().apply {
                arguments = bundle
            }
            homeFragment.apply {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, destinationFragment).commit()

                findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                    selectedItemId = R.id.nothing
                }
            }
        }
    }

    fun mapSetUi(map: GoogleMap, position : LatLng, homeFragment: FragmentActivity) {
        map.apply {
            val cameraPosition = CameraPosition.Builder().target(position).zoom(18.0f).build()

            moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            if (ActivityCompat.checkSelfPermission(
                    homeFragment, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    homeFragment, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            isMyLocationEnabled = true
            isIndoorEnabled = false
            isBuildingsEnabled = true
            uiSettings.isTiltGesturesEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = false
            val styleJson = """
                                    [
                                      {
                                        "featureType": "poi",
                                        "elementType": "labels",
                                        "stylers": [
                                          { "visibility": "off" }
                                        ]
                                      }
                                    ]
                                """.trimIndent()
            setMapStyle(MapStyleOptions(styleJson))
        }
    }



}