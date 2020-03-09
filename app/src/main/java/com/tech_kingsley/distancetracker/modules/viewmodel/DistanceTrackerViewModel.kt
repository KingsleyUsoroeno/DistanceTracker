package com.tech_kingsley.distancetracker.modules.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tech_kingsley.distancetracker.modules.db.DistanceTrackerDb
import com.tech_kingsley.distancetracker.modules.db.TrackerLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DistanceTrackerViewModel(app: Application) : AndroidViewModel(app) {

    private val database = DistanceTrackerDb.database(app)
    private val dao = database.daoTracker
    val allLocation = dao.getLocation()

    fun saveLocation(location: TrackerLocation) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertLocation(location)
            }
        }
    }

    fun getAllLocation() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.getLocations()
            }
        }
    }
}