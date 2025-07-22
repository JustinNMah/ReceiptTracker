package com.example.recipttracker.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringSet(set: Set<String>): String {
        return set.joinToString(",")
    }

    @TypeConverter
    fun toStringSet(data: String): Set<String> {
        return if (data.isBlank()) emptySet() else data.split(",").map { it.trim() }.toSet()
    }
}
