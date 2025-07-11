package com.example.recipttracker.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen
import com.example.recipttracker.ui.login.LoginScreen
import com.example.recipttracker.ui.signup.SignUpScreen
import com.example.recipttracker.ui.addReceipt.Camera
import com.example.recipttracker.ui.addReceipt.CameraRoll
import com.example.recipttracker.ui.addReceipt.AddReceipt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipttracker.ui.addReceipt.DisplayImageViewModel
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val addReceiptViewModel: DisplayImageViewModel = viewModel()
    val receiptViewModel: ReceiptViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "landing") {
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
            ReceiptListScreen(
                onCapture = { navController.navigate("camera") },
                onUpload = { navController.navigate("cameraRoll") },
                receiptViewModel
            )
        }

        composable("camera") {
            Camera(
                onFinish = { navController.navigate("addReceipt") },
                addReceiptViewModel
            )
        }
        composable("cameraRoll") {
            CameraRoll(
                onFinish = { navController.navigate("addReceipt") },
                addReceiptViewModel
            )
        }
        composable("addReceipt") {
            AddReceipt(
                onFinish = { navController.navigate("receipts") },
                addReceiptViewModel,
                receiptViewModel
            )
        }
    }
}
