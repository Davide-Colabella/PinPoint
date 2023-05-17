package com.univpm.pinpointmvvm.model.utils

import android.Manifest
import android.content.Context
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import kotlinx.coroutines.coroutineScope

class PermissionsManager(context: Context) {

    companion object {
        private val permissions = arrayOf(
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private val sharedPrefs = SharedPreferences(context)


    suspend fun checkAllPermissions(): TedPermissionResult {
        var permissionResults: TedPermissionResult
        coroutineScope {
            permissionResults =
                if (sharedPrefs.isFirstDenied()) {
                    sharedPrefs.setFirstDenied()
                    TedPermission.create()
                        .setPermissions(*permissions)
                        .setDeniedTitle("Permessi negati")
                        .setDeniedMessage("Dovresti accettare i permessi per poter utilizzare l'applicazione")
                        .check()
                } else {
                    TedPermission.create()
                        .setPermissions(*permissions)
                        .check()
                }
        }
        return permissionResults
    }

    suspend fun checkCameraPermission(): Boolean {
        var permissionResults: Boolean
        coroutineScope {
            permissionResults =
                if (sharedPrefs.isFirstDenied()) {
                    sharedPrefs.setFirstDenied()
                    TedPermission.create()
                        .setPermissions(Manifest.permission.CAMERA)
                        .setDeniedTitle("Permessi alla camera negati")
                        .setDeniedMessage("Dovresti accettare i permessi di accesso alla camera per poter utilizzare l'applicazione")
                        .checkGranted()
                } else {
                    TedPermission.create()
                        .setPermissions(Manifest.permission.CAMERA)
                        .checkGranted()
                }
        }
        return permissionResults
    }

    suspend fun checkLocationPermission(): Boolean {
        var permissionResults: Boolean
        coroutineScope {
            permissionResults =
                if (sharedPrefs.isFirstDenied()) {
                    sharedPrefs.setFirstDenied()
                    TedPermission.create()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .setDeniedTitle("Permessi alla localizzazione negati")
                        .setDeniedMessage("Dovresti accettare i permessi di accesso alla localizzazione per poter utilizzare l'applicazione")
                        .checkGranted()
                } else {
                    TedPermission.create()
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .checkGranted()
                }
        }
        return permissionResults
    }


}