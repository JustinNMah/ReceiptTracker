package com.example.recipttracker.data.repository

import android.content.Context
import com.example.recipttracker.data.local.ReceiptDao
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import android.util.Log
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

    override fun getReceipts(): Flow<List<Receipt>> {
        return dao.getReceipts()
    }

    override suspend fun getReceiptById(id: Int): Receipt? {
        return dao.getReceiptById(id)
    }

    override suspend fun getUnsyncedReceipts(): List<Receipt> {
        return dao.getUnsyncedReceipts()
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        val receiptId = dao.insertReceipt(receipt.copy(syncedWithCloud = false))
        val updatedReceipt = receipt.copy(id = receiptId.toInt())
        dao.updateReceipt(updatedReceipt)

        enqueueWifiSync(context = appContext)
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)

        // deletes from firestore
        firestore.collection("users")
            .document(receipt.userId)
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
}