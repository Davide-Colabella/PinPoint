package com.univpm.pinpointmvvm.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R

/**
 * Classe che gestisce la visualizzazione degli Snackbar
 */
object SnackbarManager {

    /**
     * Metodo che visualizza un messaggio di fallimento per un'activity
     * @param message messaggio da visualizzare
     * @param activity activity in cui visualizzare il messaggio
     * @param view view in cui visualizzare il messaggio
     */
    fun onFailure(message: String, activity: Activity, view: View) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(activity.resources.getColor(R.color.pp_light_red))
            .show()
    }

    /**
     * Metodo che visualizza un messaggio di successo per un fragment
     * @param message messaggio da visualizzare
     * @param activity activity in cui visualizzare il messaggio
     * @param view view in cui visualizzare il messaggio
     */
    fun onSuccess(message: String, fragment: Fragment) {
        Snackbar.make(
            fragment.requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(R.id.bottomNavigationView)
            .setTextColor(Color.WHITE)
            .setBackgroundTint(fragment.resources.getColor(R.color.pp_light_green))
            .show()
    }

    /**
     * Metodo che visualizza un messaggio di fallimento per un fragment
     * @param message messaggio da visualizzare
     * @param activity activity in cui visualizzare il messaggio
     * @param view view in cui visualizzare il messaggio
     */
    fun onFailure(message: String, fragment: Fragment) {
        fragment.view?.let {
            Snackbar.make(
                it,
                message,
                Snackbar.LENGTH_SHORT
            ).setAnchorView(R.id.bottomNavigationView)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(fragment.resources.getColor(R.color.pp_light_red))
                .show()
        }
    }

    /**
     * Metodo che visualizza un messaggio di warning per un fragment
     * @param message messaggio da visualizzare
     * @param activity activity in cui visualizzare il messaggio
     * @param view view in cui visualizzare il messaggio
     */
    fun onWarning(message: String, fragment: Fragment) {
        fragment.view?.let {
            Snackbar.make(
                it,
                message,
                Snackbar.LENGTH_SHORT
            ).setAnchorView(R.id.bottomNavigationView)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(fragment.resources.getColor(R.color.pp_yellow))
                .show()
        }
    }
}