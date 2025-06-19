package com.example.recipttracker.domain.repository

import com.example.recipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

// Repository for the database
interface ReceiptRepository {
    fun getReceiptsByDateDesc(): Flow<List<Receipt>>

    fun getReceiptsByDateAsc(): Flow<List<Receipt>>

    fun getReceiptsByStoreDesc(): Flow<List<Receipt>>

    fun getReceiptsByStoreAsc(): Flow<List<Receipt>>

    fun getReceiptsByCategoryDesc(): Flow<List<Receipt>>

    fun getReceiptsByCategoryAsc(): Flow<List<Receipt>>

    suspend fun getReceiptById(id: Int) : Receipt?

    suspend fun insertReceipt(receipt: Receipt)

    suspend fun deleteReceipt(receipt: Receipt)
}