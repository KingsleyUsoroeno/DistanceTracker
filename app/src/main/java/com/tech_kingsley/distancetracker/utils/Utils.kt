package com.tech_kingsley.distancetracker.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

object Utils {

    fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(context: Context): Boolean {
        if (checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return LocationManagerCompat.isLocationEnabled(locationManager)
        }
        return false
    }
}