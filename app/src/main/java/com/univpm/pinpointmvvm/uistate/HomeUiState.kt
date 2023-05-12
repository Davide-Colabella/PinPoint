package com.univpm.pinpointmvvm.uistate

data class HomeUiState(
    var permissionGranted : Boolean = false
) {
    companion object {
        fun permissionGranted() = HomeUiState(permissionGranted = true)
        fun permissionNotGranted() = HomeUiState(permissionGranted = false)
    }
}