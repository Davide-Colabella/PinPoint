package com.univpm.pinpointmvvm.model.utils

import android.content.Context

class SharedPreferences(private val context: Context) {
    companion object {
        const val PREF_FIRST_RUN = "first_run"
    }

    fun isFirstRun(): Boolean {
        val preferences = context.getSharedPreferences(PREF_FIRST_RUN, Context.MODE_PRIVATE)
        val isFirstRun = preferences.getBoolean(PREF_FIRST_RUN, true)
        if (isFirstRun) {
            preferences.edit().putBoolean(PREF_FIRST_RUN, false).apply()
        }
        return isFirstRun
    }
}