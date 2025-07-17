package com.example.recipttracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.recipttracker.ui.capture.CaptureScreen
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen
import com.example.recipttracker.ui.login.LoginScreen
import com.example.recipttracker.ui.signup.SignUpScreen

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "capture") {
        composable("landing") {
            LandingScreen(
                onLogin = { navController.navigate("login") },
                onSignUp = { navController.navigate("signup") }
            )
        }
        composable("login"){
            LoginScreen(
                onEnter = { navController.navigate("receipts") }
            )
        }
        composable("signup"){
            SignUpScreen(
                onEnter = { navController.navigate("receipts") }
            )
        }
        composable("receipts") {
            ReceiptListScreen()
        }
        composable("capture") {
            CaptureScreen()
        }
    }
}
