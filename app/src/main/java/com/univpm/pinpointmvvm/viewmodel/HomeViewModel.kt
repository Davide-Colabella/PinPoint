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
import com.univpm.pinpointmvvm.model.User
import com.univpm.pinpointmvvm.repo.UserRepository
import com.univpm.pinpointmvvm.utils.Localization
import com.univpm.pinpointmvvm.view.fragments.OtherProfileFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel per la gestione della mappa
 */
class HomeViewModel : ViewModel() {
    // Repository
    private val userRepository = UserRepository()
    // Lista degli utenti
    private val _users: MutableLiveData<List<User>> = MutableLiveData()
    val users: LiveData<List<User>> = _users
    // StateFlow per la gestione della localizzazione
    private val _locationEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val locationEnabled = _locationEnabled.asStateFlow()

    init {
        userRepository.fetchAllUsersOnDatabase(_users)
    }

    /**
     * Funzione per il caricamento dei marker sulla mappa
     * @param imageLoader: ImageLoader
     * @param homeFragment: FragmentActivity
     * @param map: GoogleMap
     * @param viewLifecycleOwner: LifecycleOwner
     * @param colorTheme: Int
     */
    fun mapAddMarkers(
        imageLoader: ImageLoader,
        homeFragment: FragmentActivity,
        map: GoogleMap,
        viewLifecycleOwner: LifecycleOwner,
        colorTheme: Int,
    ) {

        users.observe(viewLifecycleOwner) { usersList ->
            val markers = mutableListOf<Marker>()
            var loadedImages = 0

            if(map!=null){
                map.clear()
            }

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

    /**
     * Funzione per l'aggiunta del bordo al marker
     * @param bitmap: Bitmap
     * @param colorTheme: Int
     * @return Bitmap
     */
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



    /**
     * Funzione per l'aggiornamento della posizione sulla mappa
     * @param localization posizione attuale
     * @param map: GoogleMap
     */
    fun mapLocationUpdates(localization: Localization, map: GoogleMap) {
        localization.startUpdates { location ->
            val position = LatLng(location!!.latitude, location.longitude)
            val currentZoom = map.cameraPosition.zoom
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoom))

        }
    }

    /**
     * Funzione per la gestione del click sul marker
     * @param homeFragment: FragmentActivity
     * @param map: GoogleMap
     */
    fun mapSnippetClick(homeFragment: FragmentActivity, map: GoogleMap) {
        map.setOnInfoWindowClickListener { marker ->
            val user = marker.tag as User
            user.let {
                val bundle = Bundle().apply {
                    putParcelable(User.USER_OBJECT_PARCEL, user)
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
    /**
     * Funzione per la gestione dell'interfaccia della mappa
     * @param map: GoogleMap
     * @param position: LatLng
     * @param isLocationEnabled: Boolean
     * @suppress MissingPermission
     */
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

    /**
     * Funzione per la gestione della posizione.
     * Controlla se l'utente ha dato il permesso di accedere alla posizione
     * @param checkLocationPermission: Boolean
     */
    fun setLocationEnabled(checkLocationPermission: Boolean) {
        _locationEnabled.value = checkLocationPermission
    }
}