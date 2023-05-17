package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.ImageLoader
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentHomeBinding
import com.univpm.pinpointmvvm.model.utils.Localization
import com.univpm.pinpointmvvm.model.utils.SnackbarManager
import com.univpm.pinpointmvvm.view.activities.MainActivity
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val DEFAULT_POSITION = "Errore di caricamente della posizione.\nControlla i permessi o attiva il GPS."
    }
    //Localization
    private val localization: Localization by lazy { Localization(requireActivity() as MainActivity) }
    private lateinit var position: LatLng

    //Map
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    //ViewModel
    private val viewModel: HomeViewModel by viewModels()

    //ViewBinding
    private lateinit var viewBinding: FragmentHomeBinding

    //ImageLoader
    private val imageLoader: ImageLoader by lazy { Coil.imageLoader(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false)
       supportMapFragment = childFragmentManager.findFragmentById(
            R.id.map_fragment
        ) as SupportMapFragment
        lifecycleScope.launch {
            getLastPosition()
            setupMap()
        }
        return viewBinding.root
    }

    private suspend fun getLastPosition() {
        position = localization.getLastLocation()
        if (position.latitude.equals(Localization.LATITUDE_DEFAULT)
            && position.longitude.equals(Localization.LONGITUDE_DEFAULT)
        ) {
            SnackbarManager.onFailure(DEFAULT_POSITION, this)
        }
    }

    private fun setupMap() {
        supportMapFragment.getMapAsync {
            map = it
            map.apply {
                mapAddMarkers()
                viewModel.apply {
                    mapSetUi(map, position, requireActivity() as MainActivity)
                    mapSnippetClick(requireActivity(), map)
                    mapLocationUpdates(localization, map)
                }
            }
        }
    }

    private fun mapAddMarkers() {
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.addMarkers(imageLoader, requireActivity(), map, it)
        }
    }

}