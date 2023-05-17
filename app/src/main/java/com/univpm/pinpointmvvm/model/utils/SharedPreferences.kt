package com.univpm.pinpointmvvm.model.utils

import android.content.Context

class SharedPreferences(private val context: Context) {
    companion object {
        const val PREF_FIRST_RUN = "first_run"
        const val PREF_FIRST_DENIED = "first_denied"
    }

    fun isFirstRun(): Boolean {
        val preferences = context.getSharedPreferences(PREF_FIRST_RUN, Context.MODE_PRIVATE)
        val isFirstRun = preferences.getBoolean(PREF_FIRST_RUN, true)
        if (isFirstRun) {
            preferences.edit().putBoolean(PREF_FIRST_RUN, false).apply()
        }
        return isFirstRun
    }

    fun isFirstDenied(): Boolean {
        val preferences = context.getSharedPreferences(PREF_FIRST_DENIED, Context.MODE_PRIVATE)
        return preferences.getBoolean(PREF_FIRST_DENIED, true)
    }

    fun setFirstDenied() {
        val preferences = context.getSharedPreferences(PREF_FIRST_DENIED, Context.MODE_PRIVATE)
        preferences.edit().putBoolean(PREF_FIRST_DENIED, false).commit()
    }

}