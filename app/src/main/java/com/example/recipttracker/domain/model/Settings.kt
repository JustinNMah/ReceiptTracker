package com.example.recipttracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val enabledSortFields: String, // JSON string of enabled sort fields
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)