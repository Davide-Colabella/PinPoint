package com.univpm.pinpointmvvm.utils

import android.Manifest
import android.content.Context
import androidx.preference.PreferenceManager
import com.gun0912.tedpermission.coroutine.TedPermission
import com.univpm.pinpointmvvm.view.fragments.PreferencesFragment

class PermissionsManager(private val context: Context) {
    companion object {
        private val locationPerms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val sharedPrefs = SharedPreferences(context)
    suspend fun checkCameraPermission(): Boolean {

        return TedPermission.create()
            .setPermissions(Manifest.permission.CAMERA)
            .setDeniedTitle("Permessi alla camera negati")
            .setDeniedMessage("Dovresti accettare i permessi di accesso alla camera per poter utilizzare l'applicazione")
            .checkGranted()
    }

    private suspend fun checkLocationPermission(): Boolean {

        val permissionResults: Boolean = if (sharedPrefs.isFirstRun()) {
            TedPermission.create().setPermissions(*locationPerms)
                .setDeniedTitle("Permessi alla localizzazione negati")
                .setDeniedMessage("Dovresti accettare i permessi di accesso alla localizzazione per poter utilizzare l'applicazione")
                .checkGranted().apply {
                    setLocPref(this)
                }
        } else {
            TedPermission.create().setPermissions(*locationPerms).checkGranted()
        }
        return permissionResults
    }

    //setta lo switch della preferenza relativa alla posizione in base ai permessi dati al primo avvio
    private fun setLocPref(permissionResults: Boolean) {
        preferences.edit().putBoolean(PreferencesFragment.LOCATION_KEY, permissionResults)
            .apply()
    }

    // ritorna lo stato dello switch della preferenza relativa alla posizione
    private fun getLocPref(): Boolean {
        return preferences.getBoolean(PreferencesFragment.LOCATION_KEY, false)
    }

    suspend fun checkLocationPermissionForMap(): Boolean {
        return if (checkLocationPermission()) {
            getLocPref()
        } else {
            false
        }
    }

}