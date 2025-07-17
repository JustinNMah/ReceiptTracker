package com.example.recipttracker.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore

// needed this worker function so made a new folder and file
class ReceiptDeleteWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val receiptId = inputData.getString("receiptId")
        if (receiptId == null) {
            Log.e("ReceiptDeleteWorker", "Missing receiptId in input data")
            return Result.failure()
        }

        return try {
            val db = FirebaseFirestore.getInstance()
            db.collection("receipts").document(receiptId)
                .delete()
                .addOnSuccessListener {
                    Log.d("ReceiptDeleteWorker", "Successfully deleted receipt: $receiptId")
                }
                .addOnFailureListener { e ->
                    Log.e("ReceiptDeleteWorker", "Failed to delete receipt: $receiptId", e)
                }

            Result.success()
        } catch (e: Exception) {
            Log.e("ReceiptDeleteWorker", "Exception deleting receipt", e)
            Result.retry()
        }
    }
}
