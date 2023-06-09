package com.univpm.pinpointmvvm.utils

import android.Manifest
import android.content.Context
import androidx.preference.PreferenceManager
import com.gun0912.tedpermission.coroutine.TedPermission
import com.univpm.pinpointmvvm.view.fragments.PreferencesFragment

/**
 * Classe che gestisce i permessi dell'applicazione
 */
class PermissionsManager(private val context: Context) {
    companion object {
        private val locationPerms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val sharedPrefs = SharedPreferences(context)

    /**
     * Funzione che controlla i permessi dell'applicazione relativi alla camera
     * @return Boolean true se i permessi sono stati dati, false altrimenti
     */
    suspend fun checkCameraPermission(): Boolean {

        return TedPermission.create()
            .setPermissions(Manifest.permission.CAMERA)
            .setDeniedTitle("Permessi alla camera negati")
            .setDeniedMessage("Dovresti accettare i permessi di accesso alla camera per poter utilizzare l'applicazione")
            .checkGranted()
    }

    /**
     * Funzione che controlla i permessi dell'applicazione relativi alla posizione
     * @return Boolean true se i permessi sono stati dati, false altrimenti
     */
    private suspend fun checkLocationPermission(): Boolean {

        val permissionResults: Boolean = if (sharedPrefs.isFirstRun()) {
            TedPermission.create().setPermissions(*locationPerms)
                .setDeniedTitle("Permessi alla localizzazione negati")
                .setDeniedMessage("Impostazioni del profilo -> Abilita la posizione. \n Impostazioni dell'app -> Concedi i permessi alla localizzazione")
                .setDeniedCloseButtonText("Chiudi")
                .setGotoSettingButtonText("Impostazioni")
                .checkGranted().apply {
                    setLocPref(this)
                }
        } else {
            TedPermission.create().setPermissions(*locationPerms).checkGranted()
        }
        return permissionResults
    }

    /**
     * Funzione che imposta lo stato dello switch della preferenza relativa alla posizione
     * @param permissionResults Boolean true se i permessi sono stati dati, false altrimenti
     */
    private fun setLocPref(permissionResults: Boolean) {
        preferences.edit().putBoolean(PreferencesFragment.LOCATION_KEY, permissionResults)
            .apply()
    }

    /**
     * Funzione che restituisce lo stato dello switch della preferenza relativa alla posizione
     * @return Boolean true se i permessi sono stati dati, false altrimenti
     */
    private fun getLocPref(): Boolean {
        return preferences.getBoolean(PreferencesFragment.LOCATION_KEY, false)
    }

    /**
     * Funzione che controlla i permessi dell'applicazione relativi alla posizione e utili per impostare correttamente la mappa
     * @return Boolean true se i permessi sono stati dati, false altrimenti
     */
    suspend fun checkLocationPermissionForMap(): Boolean {
        return if (checkLocationPermission()) {
            getLocPref()
        } else {
            false
        }
    }

}