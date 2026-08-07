package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        FoodLogEntity::class,
        WorkoutLogEntity::class,
        StepLogEntity::class,
        WearableDeviceEntity::class,
        ChatMessageEntity::class,
        CustomRecipeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitPulseDatabase : RoomDatabase() {

    abstract fun fitPulseDao(): FitPulseDao

    companion object {
        @Volatile
        private var INSTANCE: FitPulseDatabase? = null

        fun getDatabase(context: Context): FitPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitPulseDatabase::class.java,
                    "fitpulse_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
