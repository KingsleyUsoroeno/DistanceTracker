package com.tech_kingsley.distancetracker.modules.main

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.tech_kingsley.distancetracker.R
import com.tech_kingsley.distancetracker.databinding.ActivityMainBinding
import com.tech_kingsley.distancetracker.modules.db.TrackerLocation
import com.tech_kingsley.distancetracker.modules.maps.MapsActivity
import com.tech_kingsley.distancetracker.modules.viewmodel.DistanceTrackerViewModel
import com.tech_kingsley.distancetracker.utils.Constant
import com.tech_kingsley.distancetracker.utils.Constant.FASTEST_INTERVAL
import com.tech_kingsley.distancetracker.utils.Constant.LOCATION_REQUEST_CODE
import com.tech_kingsley.distancetracker.utils.Constant.UPDATE_INTERVAL
import com.tech_kingsley.distancetracker.utils.Utils
import timber.log.Timber


class MainActivity : AppCompatActivity(), LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var viewBinding: ActivityMainBinding
    //location
    private var lastLocation: Location? = null
    private var locationRequest: LocationRequest? = null
    // location Client
    private lateinit var googleApiClient: GoogleApiClient
    // entity class
    private var trackerLocation: TrackerLocation = TrackerLocation()
    private lateinit var distanceTrackerVM: DistanceTrackerViewModel
    // Animation
    private lateinit var mRotateAnim: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setAnimator()
        startOrStopTracking()

        // Set up the animation.
        distanceTrackerVM = ViewModelProviders.of(this).get(DistanceTrackerViewModel::class.java)
    }

    private fun setAnimator() {
        mRotateAnim = AnimatorInflater.loadAnimator(this, R.animator.rotate) as AnimatorSet
        mRotateAnim.setTarget(viewBinding.androidImage)
    }

    private fun startOrStopTracking() {
        viewBinding.btnStart.setOnClickListener {
            if (Utils.checkPermission(this, Constant.LOCATION_PERMISSION)) {
                Timber.i("user granted us access to permissions")
                val lastLocation = getLastKnownLocation()
                lastLocation?.let {
                    trackerLocation.lastLocationLongitude = lastLocation.longitude
                    trackerLocation.lastLocationLatitude = lastLocation.longitude
                }
                startLocationUpdates()
                mRotateAnim.start()
                viewBinding.btnStart.visibility = View.GONE
                viewBinding.btnStop.visibility = View.VISIBLE

            } else {
                Timber.i("request permissions")
                requestLocationPermission()
                mRotateAnim.cancel()
            }
        }

        viewBinding.btnStop.setOnClickListener {
            stopLocationUpdates()
            viewBinding.btnStop.visibility = View.GONE
            viewBinding.btnStart.visibility = View.VISIBLE
        }
    }


    override fun onLocationChanged(location: Location?) {
        this.lastLocation = location
        Timber.i("latitude is ${location?.latitude}")
        Timber.i("longitude is ${location?.longitude}")
    }

    override fun onConnected(p0: Bundle?) {
        Timber.i("onConnected Called")
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient.disconnect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Timber.i("onConnectionFailed Called")
    }

    override fun onStart() {
        super.onStart()
        createGoogleApiClient()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient.disconnect()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Constant.LOCATION_PERMISSION),
            LOCATION_REQUEST_CODE
        )
    }

    private fun getLastKnownLocation(): Location? {
        Timber.i("Getting last known location")
        if (!Utils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocationPermission()
            return null
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        lastLocation?.let { location ->
            Timber.i("latitude is [${location.latitude}]")
        }
        return lastLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()

            } else {
                showToast("Cant access location without your permission")
            }
        }
    }

    private fun createGoogleApiClient() {
        Timber.i("Creating Google Api Client")
        if (!::googleApiClient.isInitialized) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
            googleApiClient.connect()
        }
    }

    private fun startLocationUpdates() {
        Timber.i("startLocationUpdates function called")
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)

        if (Utils.checkPermission(this, Constant.LOCATION_PERMISSION)) {
            if (googleApiClient.isConnected) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        Timber.i("stop location update function called")
        if (Utils.checkPermission(this, Constant.LOCATION_PERMISSION)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            lastLocation?.let {
                trackerLocation.latitude = it.latitude
                trackerLocation.longitude = it.longitude
            }

            distanceTrackerVM.saveLocation(trackerLocation)
            Timber.i("trackerLocation $trackerLocation")
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}
