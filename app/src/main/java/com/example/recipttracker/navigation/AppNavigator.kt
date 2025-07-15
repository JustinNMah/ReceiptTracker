package com.example.recipttracker.navigation

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen
import com.example.recipttracker.ui.login.LoginScreen
import com.example.recipttracker.ui.signup.SignUpScreen
import com.example.recipttracker.ui.addEditReceipt.Camera
import com.example.recipttracker.ui.addEditReceipt.CameraRoll
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptVM
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptUI
import com.example.recipttracker.ViewModels.UserViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val receiptViewModel: ReceiptViewModel = hiltViewModel()
    val modifyReceiptVM: ModifyReceiptVM = viewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "receipts") {
        composable("landing") {
            LandingScreen(
                onLogin = { navController.navigate("login") },
                onSignUp = { navController.navigate("signup") }
            )
        }
        composable("login"){
            LoginScreen(
                onEnter = { navController.navigate("receipts") },
                onBack = { navController.navigate("landing") },
                userViewModel = userViewModel,
            )
        }
        composable("signup"){
            SignUpScreen(
                onEnter = { navController.navigate("receipts") },
                onBack = { navController.navigate("landing") },
                userViewModel = userViewModel,
            )
        }
        composable("receipts") {
            ReceiptListScreen(
                onCapture = { navController.navigate("camera") },
                onUpload = { navController.navigate("cameraRoll") },
                onEdit = { navController.navigate("modifyReceiptUI") },
                receiptViewModel = receiptViewModel,
                modifyReceiptVM = modifyReceiptVM,
                onLogout = { navController.navigate("landing") },
                userViewModel = userViewModel
            )
        }
        composable("camera") {
            Camera(
                onFinish = { navController.navigate("modifyReceiptUI") },
                onFail = { navController.navigate("receipts") },
                modifyReceiptVM = modifyReceiptVM
            )
        }
        composable("cameraRoll") {
            CameraRoll(
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
    }
}
