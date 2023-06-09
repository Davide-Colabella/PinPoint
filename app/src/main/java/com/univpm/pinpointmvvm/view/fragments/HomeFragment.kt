package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.Coil
import coil.ImageLoader
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentHomeBinding
import com.univpm.pinpointmvvm.utils.Localization
import com.univpm.pinpointmvvm.utils.PermissionsManager
import com.univpm.pinpointmvvm.utils.SnackbarManager
import com.univpm.pinpointmvvm.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

/**
 * Fragment per la visualizzazione della mappa
 */
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val DEFAULT_POSITION =
            "Errore di caricamente della posizione.\nControlla i permessi o attiva il GPS."
    }

    //Localization
    private val localization: Localization by lazy { Localization(requireActivity()) }
    private lateinit var position: LatLng
    private val permissionsManager: PermissionsManager by lazy { PermissionsManager(requireActivity()) }

    //Map
    private lateinit var supportMapFragment: SupportMapFragment

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

        /**
         * In base ai permessi dell'utente, viene caricata la posizione corrente o la posizione di default
         */
        lifecycleScope.launch {
            viewModel.setLocationEnabled(permissionsManager.checkLocationPermissionForMap())
            viewModel.locationEnabled.collect {
                if (it) {
                    position = localization.getLastLocation()
                    if (position.latitude == Localization.LATITUDE_DEFAULT && position.longitude == Localization.LONGITUDE_DEFAULT) {
                        SnackbarManager.onWarning(
                            DEFAULT_POSITION, this@HomeFragment
                        )
                    }
                } else {
                    position = LatLng(Localization.LATITUDE_DEFAULT, Localization.LONGITUDE_DEFAULT)
                }
                setupMap(it)
            }
        }

        return viewBinding.root
    }

    /**
     * Metodo per impostare la mappa a livello grafico
     * @param locationEnabled: true se l'utente ha dato i permessi per la localizzazione, false altrimenti
     */
    private fun setupMap(locationEnabled: Boolean) {
        supportMapFragment.getMapAsync { map ->
            if (locationEnabled) {
                viewModel.apply {
                    mapAddMarkers(
                        imageLoader, requireActivity(), map, viewLifecycleOwner, getColorTheme()
                    )
                    mapSetUi(map, position, true)
                    mapSnippetClick(requireActivity(), map)
                    mapLocationUpdates(localization, map)
                }
            } else {
                viewModel.mapSetUi(
                    map, position, false
                )
            }

        }
    }

    /**
     * Metodo per ottenere il colore del tema attuale
     * @return Int colore del tema attuale
     */
    private fun getColorTheme(): Int {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        return if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            ContextCompat.getColor(requireContext(), R.color.primaryNight)
        } else {
            ContextCompat.getColor(requireContext(), R.color.primaryLight)
        }
    }


}