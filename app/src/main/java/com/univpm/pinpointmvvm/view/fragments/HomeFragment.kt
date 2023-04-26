package com.univpm.pinpointmvvm.view.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.addMarker
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentHomeBinding
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/*
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }


    private lateinit var mapFragment: SupportMapFragment
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        viewModel = HomeViewModel(requireActivity(), mapFragment)
    }


}
 */

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var homeViewModel: HomeViewModel = HomeViewModel()
    private lateinit var mMap: GoogleMap

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            mMap.isMyLocationEnabled = true
            setupObservers()

        }

        return binding.root
    }

    private fun setupObservers() {
        homeViewModel.users.observe(viewLifecycleOwner) { userList ->
            userList.forEach { user ->
                Glide.with(requireContext())
                    .asBitmap()
                    .load(user.image)
                    .override(50, 50)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val markerOptions = MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                .position(
                                    LatLng(
                                        user.latitude!!.toDouble(),
                                        user.longitude!!.toDouble()
                                    )
                                )
                                .title(user.username)
                            mMap.addMarker(markerOptions)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("Not yet implemented")
                        }

                    })


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

