package com.univpm.pinpointmvvm.view.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.ActivityMainBinding
import com.univpm.pinpointmvvm.model.services.Permission
import com.univpm.pinpointmvvm.view.fragments.HomeFragment
import com.univpm.pinpointmvvm.view.fragments.PostFragment
import com.univpm.pinpointmvvm.view.fragments.ProfileFragment
import com.univpm.pinpointmvvm.view.fragments.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var permission: Permission
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permission = Permission(this)
        if (permission.checkPermissions()) {
            loadContent()
        } else {
            permission.requestPermissions()
        }
    }

    private fun loadContent(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment.newInstance())
        bottomNavigationListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Permission.REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                loadContent()
            } else {
                Toast.makeText(this@MainActivity, "Permessi non concessi", Toast.LENGTH_SHORT).show()
                showRequestPermissionsDialog()
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
                R.id.profile -> replaceFragment(ProfileFragment.newInstance())
                R.id.search -> replaceFragment(SearchFragment.newInstance())
                R.id.post -> replaceFragment(PostFragment.newInstance())
                else -> {}
            }
            true
        }
    }

    private fun showRequestPermissionsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Per accedere al contenuto dell'app, devi concedere i permessi.")
            .setCancelable(false)
            .setPositiveButton("Concedi permessi") { dialog, _ ->
                dialog.dismiss()
                requestPermissions(Permission.PERMISSIONS, Permission.REQUEST_CODE)
            }
            .setNegativeButton("Esci") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Richiesta permessi")
        alert.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}