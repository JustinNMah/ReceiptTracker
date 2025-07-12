package com.example.recipttracker.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.recipttracker.ui.launch.LandingScreen
import com.example.recipttracker.ui.receiptslist.ReceiptListScreen
import com.example.recipttracker.ui.login.LoginScreen
import com.example.recipttracker.ui.signup.SignUpScreen
import com.example.recipttracker.ui.camera.Camera
import com.example.recipttracker.ui.cameraRoll.CameraRoll
import com.example.recipttracker.ui.photo.Photo
import android.util.Log

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
                onUpload = { navController.navigate("cameraRoll") }
            )
        }
        composable("camera") {
            Camera(
                onFinish = { filePath -> navController.navigate("photo/${filePath}/false") }
            )
        }
        composable("cameraRoll") {
            CameraRoll(
                onSelect = { filePath ->
                    navController.navigate("photo/${filePath}/true")
                }
            )
        }
        composable("photo/{filePath}/{isUriStr}") { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("filePath") ?: ""
            val decodedPath = Uri.decode(encodedPath)

            val isUriStr = backStackEntry.arguments?.getString("isUriStr") ?: ""
            Log.d("TAG", "Decoded path in NAV $decodedPath")
            Log.d("TAG", "isUri $isUriStr")

            val isUri = (isUriStr == "true")
            Photo(
                filePath = decodedPath,
                onFinish = { navController.navigate("receipts") },
                isUri = isUri
            )
        }
    }
}
