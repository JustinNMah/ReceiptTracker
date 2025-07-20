package com.example.recipttracker.domain.repository

import com.example.recipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow
import java.util.UUID

// Repository for the database
interface ReceiptRepository {
    fun getReceipts(userId: UUID): Flow<List<Receipt>>

    suspend fun getReceiptById(id: UUID, userId: UUID) : Receipt?

    suspend fun insertReceipt(receipt: Receipt)

    suspend fun deleteReceipt(receipt: Receipt)

    suspend fun modifyReceipt(id: UUID, store: String, amount: String, date: String, category: String, filePath: String)

    fun searchReceipts(userId: UUID, query: String): Flow<List<Receipt>>

    suspend fun getUnsyncedReceipts() : List<Receipt>
}