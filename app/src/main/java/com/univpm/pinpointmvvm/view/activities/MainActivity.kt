package com.univpm.pinpointmvvm.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.viewModelFactory
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityMainBinding
import com.univpm.pinpointmvvm.model.services.Permission
import com.univpm.pinpointmvvm.view.fragments.HomeFragment
import com.univpm.pinpointmvvm.view.fragments.PostFragment
import com.univpm.pinpointmvvm.view.fragments.ProfileFragment
import com.univpm.pinpointmvvm.view.fragments.SearchFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var permissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Permission.checkPermissionStatus(this)) {
            permissionGranted = true
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            replaceFragment(HomeFragment.newInstance())
            bottomNavigationListener()
        } else {
            Permission.requestPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Permission.handlePermissionsResult(requestCode, permissions, grantResults)) {
            permissionGranted = true
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            replaceFragment(HomeFragment.newInstance())
            bottomNavigationListener()
        } else {
            Toast.makeText(
                this,
                "Sono necessarie le autorizzazioni per la posizione",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
    private fun bottomNavigationListener(){
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment.newInstance())
                R.id.profile -> replaceFragment(ProfileFragment.newInstance())
                R.id.search -> replaceFragment(SearchFragment.newInstance())
                R.id.post -> replaceFragment(PostFragment.newInstance())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}