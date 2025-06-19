package com.example.recipttracker.ui.receiptslist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder

@Composable
fun ReceiptListScreen(
    navController: NavController, // use the navController to navigate to "Add Receipts" page
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Receipts", style = MaterialTheme.typography.headlineLarge)

        Text(
            text = "Ordered by: ${
                when (state.receiptSortOrder) {
                    is ReceiptSortOrder.DateDesc -> "Date Descending"
                    is ReceiptSortOrder.DateAsc -> "Date Ascending"
                    is ReceiptSortOrder.StoreDesc -> "Store Descending"
                    is ReceiptSortOrder.StoreAsc -> "Store Ascending"
                    is ReceiptSortOrder.CategoryDesc -> "Category Descending"
                    is ReceiptSortOrder.CategoryAsc -> "Category Ascending"
                }
            }",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(state.receipts) { receipt ->
                ReceiptItem(receipt = receipt)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ReceiptItem(receipt: Receipt) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = "Store: ${receipt.store}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Amount: \$${receipt.amount}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Date: ${receipt.date}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Category: ${receipt.category}", style = MaterialTheme.typography.bodyMedium)
    }
}
