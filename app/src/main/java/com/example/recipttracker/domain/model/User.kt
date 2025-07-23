package com.example.recipttracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val username: String,
    val hashedPassword: String,
    val createdAt: Long = System.currentTimeMillis(),
    val syncedWithCloud: Boolean = false
)
