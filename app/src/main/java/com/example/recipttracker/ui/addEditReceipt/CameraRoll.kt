package com.example.recipttracker.ui.addEditReceipt

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect

@Composable
fun CameraRoll(
    onFinish: () -> Unit,
    onFail: () -> Unit,
    modifyReceiptVM: ModifyReceiptVM
) {
    val handleClick: (Uri) -> Unit = { uri ->
        Log.d("TAG", "Uri path for photo: $uri")
        modifyReceiptVM.setReceiptToAdd(uri.toString())
        onFinish()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            handleClick(uri)
        } else {
            Log.d("TAG", "Did not choose photo")
            onFail()
        }
    }

    SideEffect {
        launcher.launch(PickVisualMediaRequest())
    }
}
