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
import com.google.android.gms.maps.SupportMapFragment
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentHomeBinding
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var binding: FragmentHomeBinding
    private var isPermissionGranted: Boolean = false
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                isPermissionGranted = true
                setUpMap()
            } else {
                //TODO: Handle permission denied
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            when {
                checkPermissions() -> {
                    setUpMap()
                }

                !isPermissionGranted -> {
                    askForPermissions()
                }
            }
        }

        return binding.root
    }

    private fun setUpMap() {
        (childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment).apply {
            HomeViewModel.Factory(this, requireActivity()).apply {
                ViewModelProvider(this@HomeFragment, this)[HomeViewModel::class.java]
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
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

