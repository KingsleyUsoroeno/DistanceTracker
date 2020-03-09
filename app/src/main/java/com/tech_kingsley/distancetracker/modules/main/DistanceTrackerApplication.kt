package com.tech_kingsley.distancetracker.modules.main

import android.app.Application
import com.tech_kingsley.distancetracker.BuildConfig
import timber.log.Timber

class DistanceTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}