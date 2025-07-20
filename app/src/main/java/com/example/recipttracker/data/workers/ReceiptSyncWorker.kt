package com.example.recipttracker.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recipttracker.data.local.ReceiptDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReceiptSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val dao = ReceiptDatabase.getInstance(applicationContext).receiptDao
        val firestore = FirebaseFirestore.getInstance()

        val unsynced = dao.getUnsyncedReceipts()

        try {
            unsynced.forEach { receipt ->
                firestore
                    .collection("users")
                    .document(receipt.userId.toString())
                    .collection("receipts")
                    .document(receipt.id.toString())
                    .set(receipt)
                    .await()

                dao.updateReceipt(receipt.copy(syncedWithCloud = true))
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed", e)
            Result.retry()
        }
    }
}