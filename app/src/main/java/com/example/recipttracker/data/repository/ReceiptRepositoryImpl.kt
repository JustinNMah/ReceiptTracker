package com.example.recipttracker.data.repository

import android.content.Context
import android.util.Log
import com.example.recipttracker.data.local.ReceiptDao
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipttracker.domain.util.ReceiptSyncWorker
import com.google.firebase.firestore.FirebaseFirestore

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao,
    private val appContext: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
): ReceiptRepository {

    override fun getReceipts(userId: Int): Flow<List<Receipt>> {
        return dao.getReceipts(userId)
    }

    override suspend fun getReceiptById(id: Int, userId: Int): Receipt? {
        return dao.getReceiptById(id, userId)
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        dao.insertReceipt(receipt.copy(syncedWithCloud = false))

        enqueueWifiSync(context = appContext)
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)

        // deletes from firestore
        firestore.collection("users")
            .document(receipt.userId.toString())
            .collection("receipts")
            .document(receipt.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Receipt deleted from Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting from Firestore", e)
            }
    }

    override suspend fun getUnsyncedReceipts(): List<Receipt> {
        return dao.getUnsyncedReceipts()
    }

    fun enqueueWifiSync(context: Context) {
        val syncRequest = OneTimeWorkRequestBuilder<ReceiptSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED) // Wifi only not data
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10, java.util.concurrent.TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
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
}