package com.univpm.pinpointmvvm.view.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.univpm.pinpointmvvm.uistate.HomeUiState
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val localization: Localization by lazy { Localization(requireActivity()) }
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var position: LatLng
    private lateinit var binding: FragmentHomeBinding
    private val imageLoader: ImageLoader by lazy {Coil.imageLoader(requireContext())}
    private val viewModel: HomeViewModel by viewModels()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            _uiState.value = HomeUiState.permissionGranted()
        } else {
            _uiState.value = HomeUiState.permissionNotGranted()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        supportMapFragment =
            (childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment)

        lifecycleScope.launch {
            uiState.collect { state ->
                if (state.permissionGranted) {
                    init()
                } else {
                    requestPermissions()
                }
            }
        }
        return binding.root
    }

    private suspend fun init() {
        position = localization.getLastLocation()
        supportMapFragment.getMapAsync {
            map = it
            map.apply {
                mapAddMarkers()
                viewModel.apply {
                    mapSetUi(map, position, requireActivity())
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

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        requestPermissionLauncher.launch(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}