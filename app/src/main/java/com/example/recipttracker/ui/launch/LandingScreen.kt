package com.example.recipttracker.ui.launch

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipttracker.ui.theme.ReciptTrackerTheme

@Composable
fun LandingScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to ReceiptTracker!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onContinue) {
            Text("Get Started")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    ReciptTrackerTheme {
        LandingScreen(onContinue = {})
    }
}
