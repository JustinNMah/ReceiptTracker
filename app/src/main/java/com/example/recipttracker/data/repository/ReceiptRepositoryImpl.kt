package com.example.recipttracker.data.repository

import android.util.Log
import com.example.recipttracker.data.local.ReceiptDao
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao
): ReceiptRepository {

    override fun getReceipts(userId: Int): Flow<List<Receipt>> {
        return dao.getReceipts(userId)
    }

    override suspend fun getReceiptById(id: Int, userId: Int): Receipt? {
        return dao.getReceiptById(id, userId)
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        dao.insertReceipt(receipt)
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)
    }

    override suspend fun modifyReceipt(
        id: Int,
        store: String,
        amount: String,
        date: String,
        category: String,
        filePath: String
    ) {
        Log.d("TAG", "Modifying receipt in ReceiptRepositoryImpl")
        val numberOfRowsAffected: Int = dao.modifyReceipt(id, store, amount, date, category, filePath)
        Log.d("TAG", "Number of rows modified: $numberOfRowsAffected")
    }

    override fun searchReceipts(userId: Int, query: String): Flow<List<Receipt>> {
        return dao.searchReceipts(userId, query)
    }

}