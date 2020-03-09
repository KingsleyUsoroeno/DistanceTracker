package com.tech_kingsley.distancetracker.modules.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackerLocation::class], version = 1, exportSchema = false)
abstract class DistanceTrackerDb : RoomDatabase() {

    abstract val daoTracker: DistanceDao

    companion object {
        private lateinit var INSTANCE: DistanceTrackerDb
        private const val DB_NAME = "DistanceTrackerDatabase"

        fun database(context: Context): DistanceTrackerDb {
            synchronized(DistanceTrackerDb::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DistanceTrackerDb::class.java, DB_NAME
                    ).build()

                }
            }
            return INSTANCE
        }
    }
}