package com.example.recipttracker.data.repository

import com.example.recipttracker.data.local.ReceiptDao
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao
): ReceiptRepository {

    override fun getReceipts(): Flow<List<Receipt>> {
        return dao.getReceipts()
    }

    override suspend fun getReceiptById(id: Int): Receipt? {
        return dao.getReceiptById(id)
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        dao.insertReceipt(receipt)
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)
    }
}