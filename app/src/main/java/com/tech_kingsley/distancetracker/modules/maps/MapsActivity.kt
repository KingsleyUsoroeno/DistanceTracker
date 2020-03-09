package com.tech_kingsley.distancetracker.modules.maps

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tech_kingsley.distancetracker.R
import com.tech_kingsley.distancetracker.modules.viewmodel.DistanceTrackerViewModel
import timber.log.Timber

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private  lateinit var distrackerVM : DistanceTrackerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        distrackerVM = ViewModelProviders.of(this).get(DistanceTrackerViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //enable the zoom in/zoom out interface on the map
        map.uiSettings.isZoomControlsEnabled = true

        //set my location enabled and draw a dot indication of my current location
        map.isMyLocationEnabled = true

        //The Android Maps API provides different map types: MAP_TYPE_NORMAL, MAP_TYPE_SATELLITE, MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID
        map.mapType = GoogleMap.MAP_TYPE_NORMAL


        distrackerVM.allLocation.observe(this, Observer {
            Timber.i("tracker location $it")

            val marker = MarkerOptions()
                .position(LatLng(it.latitude, it.longitude))
                .title("You are here")

            map.addMarker(marker)
            map.addCircle(CircleOptions().center(LatLng(it.latitude, it.longitude)).radius(50.0))

            val oldLocation = Location("oldLocation")
            oldLocation.longitude = it.lastLocationLongitude
            oldLocation.latitude = it.lastLocationLatitude
            val newLocation = Location("newLocation")
            newLocation.longitude = it.longitude
            newLocation.latitude = it.latitude

            val distance = oldLocation.distanceTo(newLocation)
            Timber.i("distance calculated is $distance")
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 16f))
        })
    }
}
