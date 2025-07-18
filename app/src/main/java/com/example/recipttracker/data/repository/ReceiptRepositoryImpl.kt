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
import com.example.recipttracker.data.workers.ReceiptDeleteWorker //links to new /workers folder
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class ReceiptRepositoryImpl(
    private val dao: ReceiptDao,
    private val context: Context,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ReceiptRepository {

    override fun getReceipts(userId: Int): Flow<List<Receipt>> {
        return dao.getReceipts(userId)
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        dao.insertReceipt(receipt)

        try {
            db.collection("receipts").document(receipt.id.toString()).set(receipt)
                .addOnSuccessListener {
                    Log.d("Firestore", "Receipt successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error writing receipt", e)
                    scheduleSync()
                }
        } catch (e: Exception) {
            Log.e("Firestore", "Exception during insert: ${e.message}")
            scheduleSync()
        }
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)

        try {
            db.collection("receipts").document(receipt.id.toString()).delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Receipt successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error deleting receipt", e)
                    scheduleDelete(receipt.id.toString())
                }
        } catch (e: Exception) {
            Log.e("Firestore", "Exception during delete: ${e.message}")
            scheduleDelete(receipt.id.toString())
        }
    }

    override suspend fun modifyReceipt(
        id: Int,
        store: String,
        amount: String,
        date: String,
        category: String,
        filePath: String
    ) {
        dao.modifyReceipt(id, store, amount, date, category, filePath)
    }


    override suspend fun getReceiptById(id: Int, userId: Int): Receipt? {
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

    private fun scheduleDelete(receiptId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<ReceiptDeleteWorker>()
            .setConstraints(constraints)
            .setInputData(androidx.work.Data.Builder().putString("receiptId", receiptId).build())
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
