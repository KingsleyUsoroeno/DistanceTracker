package com.tech_kingsley.distancetracker.modules.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DistanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(trackerLocation: TrackerLocation?)

    @Query("SELECT * FROM trackerlocation")
    fun getLocations(): LiveData<List<TrackerLocation>>

    @Query("SELECT * FROM trackerlocation")
    fun getLocation(): LiveData<TrackerLocation>

    @Query("DELETE FROM trackerlocation")
    fun deleteAllLocations()

}