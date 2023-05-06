package com.univpm.pinpointmvvm.view.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.SupportMapFragment
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentHomeBinding
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        homeViewModel = HomeViewModel(supportMapFragment, requireActivity(), viewLifecycleOwner)
    }
}

