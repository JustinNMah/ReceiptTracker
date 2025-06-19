package com.example.recipttracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipttracker.ui.theme.ReciptTrackerTheme

@Composable
fun ReceiptListScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Your Receipts",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Placeholder content
        Text("• Bob's - $34.99")
        Text("• NoFrills - $42.10")
        Text("• Walmart - $120.00")
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptListScreenPreview() {
    ReciptTrackerTheme {
        ReceiptListScreen()
    }
}

@Composable
fun ReceiptListItem() {
}
