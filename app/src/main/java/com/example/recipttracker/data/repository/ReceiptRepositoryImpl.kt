package com.example.recipttracker.data.repository

import com.example.recipttracker.data.local.ReceiptDao
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import android.util.Log


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

        // uploads to Firestore
        firestore.collection("receipts")
            .document(receipt.id.toString())
            .set(receipt)
            .addOnSuccessListener {
                Log.d("Firestore", "Receipt uploaded successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error uploading receipt", e)
            }
    }

    override suspend fun deleteReceipt(receipt: Receipt) {
        dao.deleteReceipt(receipt)

        // deletes from firestore
        firestore.collection("receipts")
            .document(receipt.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Receipt deleted from Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting from Firestore", e)
            }
    }

    // get the receipts from the cloud
    suspend fun getCloudReceipts(userId: String): List<Receipt> {
        return firestore.collection("receipts")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Receipt::class.java)
    }

}