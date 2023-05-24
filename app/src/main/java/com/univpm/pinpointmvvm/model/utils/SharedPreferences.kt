package com.univpm.pinpointmvvm.model.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.univpm.pinpointmvvm.view.fragments.PreferencesFragment

class SharedPreferences(context: Context) {
    companion object {
        const val PREF_FIRST_RUN = "first_run"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun isFirstRun(): Boolean {
        val isFirstRun = preferences.getBoolean(PREF_FIRST_RUN, true)
        if (isFirstRun) {
            preferences.edit().putBoolean(PREF_FIRST_RUN, false).apply()
        }
        return isFirstRun
    }

    fun getSelectedTheme(): String? {
        return preferences.getString(PreferencesFragment.DARK_THEME_KEY, "2")
    }

    fun setSelectedTheme(theme: String) {
        preferences.edit().putString(PreferencesFragment.DARK_THEME_KEY, theme).apply()
    }
}