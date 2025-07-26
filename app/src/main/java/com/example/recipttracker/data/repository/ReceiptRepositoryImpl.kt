package com.example.recipttracker.data.repository

import android.content.Context
import com.example.recipttracker.data.local.ReceiptDao
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipttracker.data.workers.ReceiptDeleteWorker //links to new /workers folder
import com.example.recipttracker.data.workers.ReceiptSyncWorker
import java.util.concurrent.TimeUnit
import java.util.UUID

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao,
    private val context: Context
) : ReceiptRepository {

    override fun getReceipts(userId: UUID): Flow<List<Receipt>> {
        return dao.getReceipts(userId)
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        dao.insertReceipt(receipt)
        scheduleSync()
    }

    override suspend fun insertReceiptsFromCloud(receipts: List<Receipt>) {
        for (receipt in receipts) {
            dao.insertReceipt(receipt)
        }
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)
        scheduleDelete(receipt.id.toString(), receipt.userId.toString())
    }

    override suspend fun modifyReceipt(
        id: UUID,
        store: String,
        amount: String,
        date: String,
        category: String,
        filePath: String
    ) {
        dao.modifyReceipt(id, store, amount, date, category, filePath)
    }

    override suspend fun getReceiptById(id: UUID, userId: UUID): Receipt? {
        return dao.getReceiptById(id, userId)
    }

    override suspend fun getUnsyncedReceipts(): List<Receipt> {
        return dao.getUnsyncedReceipts()
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<ReceiptSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun scheduleDelete(receiptId: String, userId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<ReceiptDeleteWorker>()
            .setConstraints(constraints)
            .setInputData(
                androidx.work.Data.Builder()
                    .putString("receiptId", receiptId)
                    .putString("userId", userId)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }


    override fun searchReceipts(userId: UUID, query: String): Flow<List<Receipt>> {
        return dao.searchReceipts(userId, query)
    }

    override suspend fun getReceiptCount(userId: UUID): Int {
        return dao.getReceiptCount(userId)
    }


}