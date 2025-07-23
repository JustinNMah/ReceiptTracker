package com.example.recipttracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipttracker.domain.model.Receipt

@Database(
    entities = [Receipt::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ReceiptDatabase: RoomDatabase() {

    abstract val receiptDao: ReceiptDao

    companion object {
        const val DATABASE_NAME = "receipts_db"

        // For ReceiptSyncWorker
        @Volatile private var INSTANCE: ReceiptDatabase? = null

        fun getInstance(context: Context): ReceiptDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ReceiptDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}
