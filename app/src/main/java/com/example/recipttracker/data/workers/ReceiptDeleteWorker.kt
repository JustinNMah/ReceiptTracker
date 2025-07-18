package com.example.recipttracker.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReceiptDeleteWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val receiptId = inputData.getString("receiptId")
        val userId = inputData.getString("userId")

        if (receiptId.isNullOrEmpty() || userId.isNullOrEmpty()) {
            Log.e("ReceiptDeleteWorker", "Missing receiptId or userId in input data")
            return Result.failure()
        }

        return try {
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(userId)
                .collection("receipts")
                .document(receiptId)
                .delete()
                .await()

            Log.d("ReceiptDeleteWorker", "Successfully deleted receipt $receiptId for user $userId")
            Result.success()
        } catch (e: Exception) {
            Log.e("ReceiptDeleteWorker", "Failed to delete receipt $receiptId for user $userId", e)
            Result.retry()
        }
    }
}
