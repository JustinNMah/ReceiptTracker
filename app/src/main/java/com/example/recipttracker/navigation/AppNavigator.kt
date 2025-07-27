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
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptVM
import com.example.recipttracker.ui.settings.SettingsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptUI
import com.example.recipttracker.ViewModels.UserViewModel
import com.example.recipttracker.ui.capture.Processing
import com.example.recipttracker.ui.receiptslist.ViewReceipt
import com.example.recipttracker.util.SessionManager

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val receiptViewModel: ReceiptViewModel = hiltViewModel()
    val modifyReceiptVM: ModifyReceiptVM = viewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val startDestination = if (SessionManager.isLoggedIn()) "receipts" else "landing"
    NavHost(navController = navController, startDestination = startDestination) {
        composable("landing") {
            LandingScreen(
                onLogin = { navController.navigate("login") },
                onSignUp = { navController.navigate("signup") }
            )
        }
        composable("login"){
            LoginScreen(
                onEnter = { navController.navigate("receipts?fromLogin=true") },
                onBack = { navController.navigate("landing") },
                userViewModel = userViewModel,
            )
        }
        composable("signup"){
            SignUpScreen(
                onEnter = { navController.navigate("receipts?fromLogin=true") },
                onBack = { navController.navigate("landing") },
                userViewModel = userViewModel,
            )
        }
        composable(
            "receipts?fromLogin={fromLogin}",
            arguments = listOf(
                navArgument("fromLogin") { defaultValue = "false" },
            )
        ) { backStackEntry ->
            val fromLogin = backStackEntry.arguments?.getString("fromLogin")?.toBoolean() ?: false

            ReceiptListScreen(
                onCapture = { navController.navigate("camera") },
                onUpload = { navController.navigate("cameraRoll") },
                onLogout = { navController.navigate("landing") },
                onView = { navController.navigate("viewReceipt") },
                onSettings = { navController.navigate("settings") },
                receiptViewModel = receiptViewModel,
                userViewModel = userViewModel,
                modifyReceiptVM = modifyReceiptVM,
                updateReceiptsFromCloud = fromLogin
            )
        }
        composable("viewReceipt") {
            ViewReceipt(
                onBack = { navController.navigate("receipts") },
                onEdit = { navController.navigate("modifyReceiptUI") },
                receiptViewModel = receiptViewModel,
                modifyReceiptVM = modifyReceiptVM
            )
        }
        composable("camera") {
            Camera(
                onFinish = { navController.navigate("processing") },
                onFail = { navController.navigate("receipts") },
                modifyReceiptVM = modifyReceiptVM
            )
        }
        composable("cameraRoll") {
            CameraRoll(
                onFinish = { navController.navigate("processing") },
                onFail = { navController.navigate("receipts") },
                modifyReceiptVM = modifyReceiptVM
            )
        }
        composable("processing") {
            Processing(
                onFinish = { navController.navigate("modifyReceiptUI") },
                onFail = { navController.navigate("receipts") },
                modifyReceiptVM = modifyReceiptVM
            )
        }
        composable("modifyReceiptUI") {
            ModifyReceiptUI(
                onFinish = { navController.navigate("receipts") },
                modifyReceiptVM = modifyReceiptVM,
                receiptViewModel = receiptViewModel,
                userViewModel = userViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.navigate("receipts") },
                receiptViewModel = receiptViewModel
            )
        }
    }
}