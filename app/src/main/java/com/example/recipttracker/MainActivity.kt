package com.example.recipttracker.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.recipttracker.ui.theme.ReceiptTrackerTheme
import com.example.recipttracker.navigation.AppNavigator
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Call test function
        testAddReceipt()

        setContent {
            ReceiptTrackerTheme {
                AppNavigator()
            }
        }
    }

    private fun testAddReceipt() {
        val db = FirebaseFirestore.getInstance()

        // Sample receipt data
        val receipt = hashMapOf(
            "title" to "Test Grocery Store",
            "amount" to 19.99,
            "date" to "2025-07-11"
        )

        db.collection("receipts")
            .add(receipt)
            .addOnSuccessListener { docRef ->
                Log.d("FirestoreTest", "Document added with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreTest", "Error adding document", e)
            }
    }
}
