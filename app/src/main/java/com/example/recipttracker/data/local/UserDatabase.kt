package com.example.recipttracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipttracker.domain.model.User

@Database(
    entities = [User::class],
    version = 3
)
abstract class UserDatabase: RoomDatabase() {

    abstract val userDao: UserDao

    companion object {
        const val DATABASE_NAME = "users_db"
    }
}