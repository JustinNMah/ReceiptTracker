package com.example.recipttracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recipttracker.domain.model.Receipt

@Database(
    entities = [Receipt::class],
    version = 3
)
abstract class ReceiptDatabase: RoomDatabase() {

    abstract val receiptDao: ReceiptDao

    companion object {
        const val DATABASE_NAME = "receipts_db"
    }
}