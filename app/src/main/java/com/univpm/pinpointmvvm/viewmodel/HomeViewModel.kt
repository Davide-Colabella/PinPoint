package com.univpm.pinpointmvvm.viewmodel

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.CropTransformation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.data.User
import com.univpm.pinpointmvvm.model.repo.MapRepository
import com.univpm.pinpointmvvm.model.services.Localization
import com.univpm.pinpointmvvm.view.activities.UserProfileActivity
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission", "StaticFieldLeak", "PotentialBehaviorOverride")
class HomeViewModel(
    private val supportMapFragment: SupportMapFragment,
    private val fragment: FragmentActivity,
    private val viewLifecycleOwner: LifecycleOwner,
) : ViewModel() {
    private lateinit var mapBounds: LatLngBounds
    private lateinit var position: LatLng
    private lateinit var map: GoogleMap
    private val mapRepository = MapRepository()
    private val localization: Localization = Localization(fragment)
    private val users: LiveData<List<User>> = mapRepository.getAllUsers()

    init {
        /*
            Aspetta che si conosca la posizione.
            Crea i confini di visualizzazione della mappa.
            A mappa pronta, setup della mappa
         */
        viewLifecycleOwner.lifecycleScope.launch {
            position = localization.getLastLocation()
            mapBounds = LatLngBounds(
                LatLng(position.latitude, position.longitude),  // SW bounds
                LatLng(position.latitude, position.longitude) // NE bounds
            )
            supportMapFragment.getMapAsync {
                map = it
                //muovi la camera alla posizione
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f))
                mapOptions()

                addMarkers()
                snippetListener()
                localizationUpdates()
            }
        }
    }

    private fun addMarkers() {
        //aggiunta dei marker sulla mappa
        val imageLoader = Coil.imageLoader(fragment)
        users.observe(viewLifecycleOwner) { userList ->
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

    private fun localizationUpdates() {
        //inizio dell'aggiornamento della posizione
        localization.startUpdates { location ->
            position = LatLng(location!!.latitude, location.longitude)
//            mapBounds = LatLngBounds(
//                LatLng(position.latitude, position.longitude),  // SW bounds
//                LatLng(position.latitude, position.longitude)  // NE bounds
//            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f))
//            map.setLatLngBoundsForCameraTarget(mapBounds)
        }
    }

    private fun snippetListener() {
        //definisce il click sullo snippet del marker
        map.setOnInfoWindowClickListener { marker ->
            val user = marker.tag as User
            val intent = Intent(fragment, UserProfileActivity::class.java).apply {
                putExtra(Constants.USER_OBJECT_PARCEL, user)
            }
            fragment.startActivity(intent)
        }
    }

    private fun mapOptions() {
        //determina i confini di visualizzazione della mappa
//        map.setLatLngBoundsForCameraTarget(mapBounds)
        //definisci le opzioni di visualizzazione
        map.isMyLocationEnabled = true
        map.isIndoorEnabled = false
        map.isBuildingsEnabled = true
        map.uiSettings.isCompassEnabled = true
//        //rimuove il pulsante per aprire google maps
        map.uiSettings.isMapToolbarEnabled = false
    }


    override fun onCleared() {
        super.onCleared()
        localization.stopUpdates()
    }
}