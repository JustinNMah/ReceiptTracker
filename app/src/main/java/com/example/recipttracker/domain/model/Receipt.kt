package com.example.recipttracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Entity
data class Receipt(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val userId: UUID, // Maps to the userId
    val store: String,
    val amount: String,
    val date: String, // format as: yyyy-MM-dd
    val category: String,
    val filePath: String,
    val syncedWithCloud: Boolean = false,
    val data: List<String> = emptyList(),
) {
    // get month from dates
    val monthYear: String
        get() {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedDate = dateFormat.parse(date)
                val calendar = Calendar.getInstance()
                calendar.time = parsedDate!!

                val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                monthFormat.format(calendar.time)
            } catch (e: Exception) {
                "Unknown"
            }
        }

    val parsedDate: Date?
        get() {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.parse(date)
            } catch (e: Exception) {
                null
            }
        }
}
