package com.tech_kingsley.distancetracker.modules.db

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackerLocation(
    @NonNull @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var lastLocationLongitude: Double = 0.0,
    var lastLocationLatitude: Double = 0.0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
