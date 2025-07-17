package com.example.recipttracker.domain.repository

import com.example.recipttracker.domain.model.Receipt
import kotlinx.coroutines.flow.Flow

// Repository for the database
interface ReceiptRepository {
    fun getReceipts(userId: Int): Flow<List<Receipt>>

    suspend fun getReceiptById(id: Int, userId: Int) : Receipt?

    suspend fun insertReceipt(receipt: Receipt)

    suspend fun deleteReceipt(receipt: Receipt)

    suspend fun modifyReceipt(id: Int, store: String, amount: String, date: String, category: String, filePath: String)

    fun searchReceipts(userId: Int, query: String): Flow<List<Receipt>>
}