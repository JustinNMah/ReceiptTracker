package com.example.recipttracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt)

    @Update
    suspend fun updateReceipt(receipt: Receipt) // TODO: Consider removing this? Cause insertReceipt has conflict strategy of replace

    @Delete
    suspend fun deleteReceipt(receipt: Receipt) // TODO: Maybe change to deleteReceiptById

    @Query("SELECT * FROM receipt")
    fun getReceipts(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipt WHERE id = :id")
    fun getReceiptById(id: Int): Receipt?

}