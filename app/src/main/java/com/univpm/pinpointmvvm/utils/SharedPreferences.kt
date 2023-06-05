package com.univpm.pinpointmvvm.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.univpm.pinpointmvvm.view.fragments.PreferencesFragment

/**
 * Classe che gestisce le preferenze dell'applicazione
 */
class SharedPreferences(context: Context) {
    companion object {
        const val PREF_FIRST_RUN = "first_run"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Funzione che controlla se l'applicazione è stata avviata per la prima volta
     * @return Boolean true se è la prima volta che l'applicazione viene avviata, false altrimenti
     */
    fun isFirstRun(): Boolean {
        val isFirstRun = preferences.getBoolean(PREF_FIRST_RUN, true)
        if (isFirstRun) {
            preferences.edit().putBoolean(PREF_FIRST_RUN, false).apply()
        }
        return isFirstRun
    }

    /**
     * Funzione che restituisce il tema selezionato
     * @return String tema selezionato
     */
    fun getSelectedTheme(): String? {
        return preferences.getString(PreferencesFragment.DARK_THEME_KEY, "2")
    }

    /**
     * Funzione che imposta il tema selezionato
     * @param theme String tema selezionato
     */
    fun setSelectedTheme(theme: String) {
        preferences.edit().putString(PreferencesFragment.DARK_THEME_KEY, theme).apply()
    }
}