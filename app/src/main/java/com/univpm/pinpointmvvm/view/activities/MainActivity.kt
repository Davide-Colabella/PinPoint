package com.univpm.pinpointmvvm.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityMainBinding
import com.univpm.pinpointmvvm.view.fragments.CurrentProfileFragment
import com.univpm.pinpointmvvm.view.fragments.FeedFragment
import com.univpm.pinpointmvvm.view.fragments.HomeFragment
import com.univpm.pinpointmvvm.view.fragments.PostFragment
import com.univpm.pinpointmvvm.view.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment.newInstance())
        bottomNavigationListener()
    }

    override fun onBackPressed() {
        if (binding.bottomNavigationView.selectedItemId == R.id.home) {
            super.onBackPressed()
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.home
        }
    }

    private fun bottomNavigationListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment.newInstance())
                R.id.profile -> replaceFragment(CurrentProfileFragment.newInstance())
                R.id.search -> replaceFragment(SearchFragment.newInstance())
                R.id.post -> replaceFragment(PostFragment.newInstance())
                R.id.feed -> replaceFragment(FeedFragment.newInstance())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }


}