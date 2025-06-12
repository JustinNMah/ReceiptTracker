package com.example.recipttracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.recipttracker.ui.screen.LandingScreen
import com.example.recipttracker.ui.screen.ReceiptListScreen

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") {
            LandingScreen(onContinue = {
                navController.navigate("receipts")
            })
        }
        composable("receipts") {
            ReceiptListScreen()
        }
    }
}
