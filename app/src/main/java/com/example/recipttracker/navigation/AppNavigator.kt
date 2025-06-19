package com.example.recipttracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen

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
            ReceiptListScreen(navController)
        }
    }
}
