package com.example.recipttracker.domain.repository

import com.example.recipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

// Repository for the database
interface ReceiptRepository {
    fun getReceipts(): Flow<List<Receipt>>

    suspend fun getReceiptById(id: Int) : Receipt?

    suspend fun insertReceipt(receipt: Receipt)

    suspend fun deleteReceipt(receipt: Receipt)

    suspend fun getUnsyncedReceipts() : List<Receipt>
}