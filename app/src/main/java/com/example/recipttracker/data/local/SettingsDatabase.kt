package com.example.recipttracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipttracker.domain.model.Settings

@Database(
    entities = [Settings::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SettingsDatabase: RoomDatabase() {

    abstract val settingsDao: SettingsDao

    companion object {
        const val DATABASE_NAME = "settings_db"

        @Volatile private var INSTANCE: SettingsDatabase? = null

        fun getInstance(context: Context): SettingsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SettingsDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}