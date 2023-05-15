package com.univpm.pinpointmvvm.model.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R

object SnackbarManager {

    fun onSuccess(message: String, activity : Activity, view: View) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(activity.resources.getColor(R.color.pp_light_green))
            .show()
    }

    fun onFailure(message: String, activity : Activity, view: View) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(activity.resources.getColor(R.color.pp_light_red))
            .show()
    }

    fun onWarning(message: String, activity : Activity, view: View) {
        Snackbar.make(
            view,
            message,
            Snackbar.LENGTH_SHORT
        )
            .setTextColor(Color.WHITE)
            .setBackgroundTint(activity.resources.getColor(R.color.pp_yellow))
            .show()
    }

    fun onSuccess(message: String, fragment : Fragment) {
        Snackbar.make(
            fragment.requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(R.id.bottomNavigationView)
            .setTextColor(Color.WHITE)
            .setBackgroundTint(fragment.resources.getColor(R.color.pp_light_green))
            .show()
    }

    fun onFailure(message: String, fragment : Fragment) {
        Snackbar.make(
            fragment.requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(R.id.bottomNavigationView)
            .setTextColor(Color.WHITE)
            .setBackgroundTint(fragment.resources.getColor(R.color.pp_light_red))
            .show()
    }

    fun onWarning(message: String, fragment : Fragment) {
        Snackbar.make(
            fragment.requireView(),
            message,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(R.id.bottomNavigationView)
            .setTextColor(Color.WHITE)
            .setBackgroundTint(fragment.resources.getColor(R.color.pp_yellow))
            .show()
    }
}