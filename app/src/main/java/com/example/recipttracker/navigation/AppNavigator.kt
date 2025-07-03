package com.example.recipttracker.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen
import com.example.recipttracker.ui.login.LoginScreen
import com.example.recipttracker.ui.signup.SignUpScreen
import com.example.recipttracker.ui.camera.Camera
import com.example.recipttracker.ui.photo.Photo

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

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
                onCapture = { navController.navigate("camera") }
            )
        }
        composable("camera") {
            Camera(
                onFinish = { filePath -> navController.navigate("photo/${filePath}") }
            )
        }
        composable("photo/{filePath}") { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("filePath") ?: ""
            val decodedPath = Uri.decode(encodedPath)
            Photo(
                filePath = decodedPath,
                onFinish = { navController.navigate("receipts") }
            )
        }
    }
}
