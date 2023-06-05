package com.univpm.pinpointmvvm.view.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.univpm.pinpointmvvm.R

/**
 * Fragment per la visualizzazione delle preferenze dell'applicazione
 */
class PreferencesFragment : PreferenceFragmentCompat() {

    companion object {
        const val LOCATION_KEY = "localization_preference"
        const val DARK_THEME_KEY = "list_theme_preference"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val darkThemePreference = findPreference<ListPreference>(DARK_THEME_KEY)
        darkThemePreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->

            val selectedValue = newValue.toString()
            applyDarkMode(selectedValue)

            true // Restituisci true per indicare che hai gestito il cambiamento di preferenza
        }
    }

    /**
     * Applica il tema in base alla modalità scelta
     * @param selectedValue modalità scelta
     */
    fun applyDarkMode(selectedValue: String) {
        val nightMode = when (selectedValue) {
            "0" -> AppCompatDelegate.MODE_NIGHT_NO
            "1" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

}