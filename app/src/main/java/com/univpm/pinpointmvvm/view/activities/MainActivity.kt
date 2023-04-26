package com.univpm.pinpointmvvm.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.viewModelFactory
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityMainBinding
import com.univpm.pinpointmvvm.model.services.Localization
import com.univpm.pinpointmvvm.view.fragments.HomeFragment
import com.univpm.pinpointmvvm.view.fragments.ProfileFragment
import com.univpm.pinpointmvvm.view.fragments.SearchFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment.newInstance())
        bottomNavigationListener()
    }

    private fun bottomNavigationListener(){
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment.newInstance())
                R.id.profile -> replaceFragment(ProfileFragment.newInstance())
                R.id.search -> replaceFragment(SearchFragment.newInstance())
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