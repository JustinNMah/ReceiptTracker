package com.example.recipttracker.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen
import com.example.recipttracker.ui.login.LoginScreen
import com.example.recipttracker.ui.signup.SignUpScreen
import com.example.recipttracker.ui.addEditReceipt.Camera
import com.example.recipttracker.ui.addEditReceipt.CameraRoll
import com.example.recipttracker.ui.addEditReceipt.AddEditReceipt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipttracker.ui.addEditReceipt.ReceiptToEditOrAdd
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val receiptToEditOrAdd: ReceiptToEditOrAdd = viewModel()
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
                onEnter = { navController.navigate("receipts") },
                onBack = { navController.navigate("landing") }
            )
        }
        composable("signup"){
            SignUpScreen(
                onEnter = { navController.navigate("receipts") },
                onBack = { navController.navigate("landing") }
            )
        }
        composable("receipts") {
            ReceiptListScreen(
                onCapture = { navController.navigate("camera") },
                onUpload = { navController.navigate("cameraRoll") },
                onEdit = { navController.navigate("addEditReceipt") },
                receiptToEditOrAdd,
                receiptViewModel
            )
        }

        composable("camera") {
            Camera(
                onFinish = { navController.navigate("addEditReceipt") },
                receiptToEditOrAdd
            )
        }
        composable("cameraRoll") {
            CameraRoll(
                onFinish = { navController.navigate("addEditReceipt") },
                receiptToEditOrAdd
            )
        }
        composable("addEditReceipt") {
            AddEditReceipt(
                onFinish = { navController.navigate("receipts") },
                receiptToEditOrAdd,
                receiptViewModel
            )
        }
    }
}
