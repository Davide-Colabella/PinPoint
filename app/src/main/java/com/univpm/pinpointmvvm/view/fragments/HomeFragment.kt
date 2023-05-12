package com.univpm.pinpointmvvm.view.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.ImageLoader
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentHomeBinding
import com.univpm.pinpointmvvm.model.utils.Localization
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var localization: Localization
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var position: LatLng
    private lateinit var imageLoader: ImageLoader
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private var isPermissionGranted: Boolean = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isPermissionGranted = true
        } else {
            //TODO: Handle permission denied
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        imageLoader = Coil.imageLoader(requireContext())
        supportMapFragment =
            (childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment)

        lifecycleScope.launch {
            when {
                checkPermissions() -> {
                    localization = Localization(requireActivity())
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
                !isPermissionGranted -> {
                    askForPermissions()
                }
            }
        }
        return binding.root
    }

    private fun mapAddMarkers() {
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.addMarkers(imageLoader, requireActivity(), map, it)
        }
    }


    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions() {
        if (!isPermissionGranted) {
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }
}