package com.example.recipttracker.ui.addReceipt

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.SideEffect

@Composable
fun CameraRoll(
    onFinish: () -> Unit,
    displayImageViewModel: DisplayImageViewModel
) {
    val handleClick: (Uri) -> Unit = { uri ->
        Log.d("TAG", "Uri path for photo: ${uri.toString()}")
        displayImageViewModel.changeUriPath(uri.toString())
        onFinish()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            handleClick(uri)
        } else {
            Log.d("TAG", "Did not choose photo")
        }
    }

    SideEffect {
        launcher.launch(PickVisualMediaRequest())
    }
}
