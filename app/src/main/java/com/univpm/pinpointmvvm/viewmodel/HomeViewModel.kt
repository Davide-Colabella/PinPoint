package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.univpm.pinpointmvvm.model.data.MapOptions
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.MapRepository
import com.univpm.pinpointmvvm.model.services.Localization
import com.univpm.pinpointmvvm.view.activities.OtherUserProfileActivity
import kotlinx.coroutines.launch

// TODO gestire il continuo ascolto della posizione del dispositivo e dell'aggiornamento della posizione dell'utente


@SuppressLint("MissingPermission", "StaticFieldLeak")
class HomeViewModel(
    private val mapFragment: SupportMapFragment,
    private val requireActivity: FragmentActivity,
    private val viewLifecycleOwner: LifecycleOwner
) : ViewModel() {
    private val mapOptions = MapOptions()
    private val mapRepository = MapRepository()
    private lateinit var map: GoogleMap
    private val localization: Localization = Localization(requireActivity)

    private val users: LiveData<List<User>> = mapRepository.getAllUsers()

    init {
        setupMapOptions()
        setupCameraMovementObserver()
        setupMarkerObserver()
    }


    private fun setupMapOptions() {

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            map.isMyLocationEnabled = mapOptions.isMyLocationEnabled
            //map.uiSettings.isZoomControlsEnabled = mapOptions.isZoomControlsEnabled
            //map.uiSettings.isZoomGesturesEnabled = mapOptions.isZoomGestureEnabled
            map.uiSettings.isMapToolbarEnabled = mapOptions.isMapToolbarEnabled
            viewLifecycleOwner.lifecycleScope.launch {
                val position = localization.getLastLocation()!!
                val mapViewBounds = LatLngBounds(
                    LatLng(position.latitude, position.longitude),  // SW bounds
                    LatLng(position.latitude, position.longitude) // NE bounds
                )
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, mapOptions.zoomLevel))
                map.setLatLngBoundsForCameraTarget(mapViewBounds)
            }
            map.setOnInfoWindowClickListener { marker ->
                val userFromMarker = marker.tag as User?
                val intent = Intent(requireActivity, OtherUserProfileActivity::class.java)
                val bundle = Bundle().apply {
                        putSerializable("USER_OBJECT", userFromMarker)
                }
                intent.putExtras(bundle)
                startActivity(requireActivity as Context, intent, bundle)
            }
        }
    }

    private fun setupCameraMovementObserver() {
        localization.startUpdates { location ->
            val position = LatLng(location!!.latitude, location.longitude)
            val mapViewBounds = LatLngBounds(
                LatLng(position.latitude, position.longitude),  // SW bounds
                LatLng(position.latitude, position.longitude)  // NE bounds
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, mapOptions.zoomLevel))
            map.setLatLngBoundsForCameraTarget(mapViewBounds)
        }
    }


    private fun setupMarkerObserver() {

        users.observe(viewLifecycleOwner) { userList ->
            map.clear()
            userList.forEach { user ->
                val position = LatLng(user.latitude!!.toDouble(), user.longitude!!.toDouble())
                val marker = map.addMarker(
                    MarkerOptions().position(position).title(user.username)
                )
                marker!!.tag = user
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        localization.stopUpdates()
    }
}