package com.univpm.pinpointmvvm.model.data

data class MapOptions(
    val zoomLevel: Float = 18.0f,
    val tilt: Float = 45.0f,
    val bearing: Float = 0f,
    val isMyLocationEnabled: Boolean = true,
    val isCompassEnabled: Boolean = true,
    val isRotateGestureEnabled: Boolean = true,
    val isScrollGestureEnabled: Boolean = false,
    val isTiltGestureEnabled: Boolean = true,
    val isZoomControlsEnabled: Boolean = false,
    val isZoomGestureEnabled: Boolean = false,
    val minZoomLevel: Float = 15.0f,
    val maxZoomLevel: Float = 15.0f,
    val isMapToolbarEnabled : Boolean = false,
)