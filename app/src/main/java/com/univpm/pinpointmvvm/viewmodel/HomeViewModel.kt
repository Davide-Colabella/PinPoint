package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
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
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.model.utils.Localization
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@SuppressLint("StaticFieldLeak", "PotentialBehaviorOverride")
class HomeViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _users: MutableLiveData<List<User>> = MutableLiveData()
    val users: LiveData<List<User>> = _users
    private val _locationEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val locationEnabled = _locationEnabled.asStateFlow()

    init {
        userRepository.fetchAllUsersOnDatabase(_users)
    }

    //aggiunta dei marker sulla mappa
    fun mapAddMarkers(
        imageLoader: ImageLoader,
        homeFragment: FragmentActivity,
        map: GoogleMap,
        viewLifecycleOwner: LifecycleOwner,
        colorTheme: Int,
    ) {
        map.clear()
        users.observe(viewLifecycleOwner) { usersList ->

            val markers = mutableListOf<Marker>()
            var loadedImages = 0
            usersList.forEach { user ->
                val position = LatLng(user.latitude!!.toDouble(), user.longitude!!.toDouble())
                val marker = map.addMarker(
                    MarkerOptions().position(position).title(user.username)
                )
                marker!!.isVisible = false
                marker.tag = user
                markers.add(marker)
                val request = ImageRequest.Builder(homeFragment).data(user.image).transformations(
                    CircleCropTransformation(),
                    CropTransformation(CropTransformation.CropType.CENTER)
                ).size(150).target { drawable ->
                    val originalBitmap = drawable.toBitmap()
                    val borderedBitmap = addBorderToBitmap(originalBitmap, colorTheme)
                    val markerIcon = BitmapDescriptorFactory.fromBitmap(borderedBitmap)
                    marker.setIcon(markerIcon)
                    loadedImages++
                    if (loadedImages == usersList.size) {
                        markers.forEach { mapMarker ->
                            mapMarker.isVisible = true
                        }
                    }
                }.build()
                imageLoader.enqueue(request)
            }

        }

    }

    private fun addBorderToBitmap(bitmap: Bitmap, colorTheme: Int): Bitmap {
        val borderWidth = 5
        val borderColor = colorTheme
        val width = bitmap.width + borderWidth * 2
        val height = bitmap.height + borderWidth * 2
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        val paint = Paint()
        paint.color = borderColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        val radius = Math.min(width, height) / 2f
        val cx = width / 2f
        val cy = height / 2f

        canvas.drawCircle(cx, cy, radius, paint)
        canvas.drawBitmap(bitmap, borderWidth.toFloat(), borderWidth.toFloat(), null)

        return outputBitmap
    }




    fun mapLocationUpdates(localization: Localization, map: GoogleMap) {
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
            user.let {
                val bundle = Bundle().apply {
                    putParcelable(Constants.USER_OBJECT_PARCEL, user)
                }
                val fragment = OtherProfileFragment()
                fragment.arguments = bundle
                homeFragment.supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun mapSetUi(map: GoogleMap, position: LatLng, isLocationEnabled: Boolean) {
        map.apply {
            if (position.latitude.equals(Localization.LATITUDE_DEFAULT) && position.longitude.equals(
                    Localization.LONGITUDE_DEFAULT
                )
            ) {
                val cameraPosition = CameraPosition.Builder().target(position).zoom(15.0f).build()
                moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else {
                val cameraPosition = CameraPosition.Builder().target(position).zoom(18.0f).build()
                moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            isMyLocationEnabled = isLocationEnabled
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

    fun setLocationEnabled(checkLocationPermission: Boolean) {
        _locationEnabled.value = checkLocationPermission
    }
}