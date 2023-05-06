package com.univpm.pinpointmvvm.viewmodel

import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.model.constants.Constants
import com.univpm.pinpointmvvm.model.repo.UserRepository
import com.univpm.pinpointmvvm.view.fragments.HomeFragment

class PostViewModel : ViewModel() {

    private var userRepository = UserRepository()

    fun uploadPost(imageUri: Uri, description: String, view: View, fragment: FragmentActivity) {
        userRepository.uploadPost(imageUri, description)
        Snackbar.make(view, Constants.POST_SUCCESSFULLY_UPLOADED, Snackbar.LENGTH_SHORT).show()
        closeFragment(fragment)
    }


    private fun closeFragment(fragment: FragmentActivity) {
        val destinationFragment = HomeFragment.newInstance()
        fragment.apply {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, destinationFragment)
                .commit()

            findViewById<BottomNavigationView>(R.id.bottomNavigationView).apply {
                selectedItemId = R.id.home
            }
        }
    }
}

