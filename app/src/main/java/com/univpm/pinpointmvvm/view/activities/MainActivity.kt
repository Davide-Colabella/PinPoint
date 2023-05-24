package com.univpm.pinpointmvvm.view.activities

import android.app.UiModeManager
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityMainBinding
import com.univpm.pinpointmvvm.model.utils.SharedPreferences
import com.univpm.pinpointmvvm.view.fragments.CurrentProfileFragment
import com.univpm.pinpointmvvm.view.fragments.FeedFragment
import com.univpm.pinpointmvvm.view.fragments.HomeFragment
import com.univpm.pinpointmvvm.view.fragments.PostFragment
import com.univpm.pinpointmvvm.view.fragments.PreferencesFragment
import com.univpm.pinpointmvvm.view.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedPreferences: SharedPreferences by lazy { SharedPreferences(this) }
    private val preferencesFragment = PreferencesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val theme = sharedPreferences.getSelectedTheme()
        if (theme != null) {
            preferencesFragment.applyDarkMode(theme)
            sharedPreferences.setSelectedTheme(theme)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigationColor()
        replaceFragment(HomeFragment.newInstance())
        bottomNavigationListener()
    }

    private fun setBottomNavigationColor() {
        binding.bottomNavigationView.backgroundTintList =
            ColorStateList.valueOf(getBottomNavigationViewBackground()[0])
        binding.bottomNavigationView.itemTextColor =
            ColorStateList.valueOf(getBottomNavigationViewBackground()[1])
        binding.bottomNavigationView.itemIconTintList =
            ColorStateList.valueOf(getBottomNavigationViewBackground()[1])
    }

    private fun getBottomNavigationViewBackground(): IntArray {
        val nightMode = AppCompatDelegate.getDefaultNightMode()

        return when (nightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> intArrayOf(
                ContextCompat.getColor(this, R.color.primaryNight),
                ContextCompat.getColor(this, R.color.accentNight)
            )
            AppCompatDelegate.MODE_NIGHT_NO -> intArrayOf(
                ContextCompat.getColor(this, R.color.secondaryLight),
                ContextCompat.getColor(this, R.color.accentLight)
            )
            else -> {
                val uiModeManager = this.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                val isSystemInDarkMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES

                if (isSystemInDarkMode) {
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.primaryNight),
                        ContextCompat.getColor(this, R.color.accentNight)
                    )
                } else {
                    intArrayOf(
                        ContextCompat.getColor(this, R.color.secondaryLight),
                        ContextCompat.getColor(this, R.color.accentLight)
                    )
                }
            }
        }
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