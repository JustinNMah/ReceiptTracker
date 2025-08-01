package com.example.recipttracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.recipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ReceiptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt)

    @Update
    suspend fun updateReceipt(receipt: Receipt)

    @Delete
    suspend fun deleteReceipt(receipt: Receipt)

    @Query("SELECT * FROM receipt WHERE userId = :userId")
    fun getReceipts(userId: UUID): Flow<List<Receipt>>

    @Query("SELECT * FROM receipt WHERE id = :id AND userId = :userId")
    suspend fun getReceiptById(id: UUID, userId: UUID): Receipt?

    @Query(
        "UPDATE receipt SET store = :store, amount = :amount, date = :date, category = :category, filePath = :filePath WHERE id = :id"
    )
    suspend fun modifyReceipt(id: UUID, store: String, amount: String, date: String, category: String, filePath: String): Int

    @Query("""
        SELECT * FROM receipt 
        WHERE userId = :userId AND (
            store LIKE '%' || :query || '%' OR
            category LIKE '%' || :query || '%' OR
            date LIKE '%' || :query || '%' OR
            data LIKE '%' || :query || '%'
        )
    """)
    fun searchReceipts(userId: UUID, query: String): Flow<List<Receipt>>

    @Query("SELECT * FROM receipt WHERE syncedWithCloud = 0")
    suspend fun getUnsyncedReceipts(): List<Receipt>

    @Query("SELECT COUNT(*) FROM receipt WHERE userId = :userId")
    suspend fun getReceiptCount(userId: UUID): Int

    @Query("""
    SELECT SUM(CAST(amount AS FLOAT)) 
    FROM receipt 
    WHERE userId = :userId 
    AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')
""")
    suspend fun getMonthlyTotal(userId: UUID): Float?



}