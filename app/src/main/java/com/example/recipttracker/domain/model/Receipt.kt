package com.example.recipttracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Receipt(
    @PrimaryKey val id: Int? = null,
    val store: String,
    val amount: Float,
    val date: String, // TODO: might need to change this type
    val category: String
)
