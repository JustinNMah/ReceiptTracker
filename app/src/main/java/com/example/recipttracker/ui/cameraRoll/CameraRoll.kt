package com.example.recipttracker.ui.cameraRoll

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun CameraRoll(
    onSelect: (String) -> Unit
) {
    val handleClick: (Uri) -> Unit = { uri ->
        Log.d("TAG", "Uri path for photo: ${uri.toString()}")
        val encodedPath = Uri.encode(uri.toString())
        Log.d("TAG", "Decoded Uri path for photo: $encodedPath")
        onSelect(Uri.encode(uri.toString()))
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            handleClick(uri)
        } else {
            Log.d("TAG", "Did not choose photo")
        }
    }

    SideEffect{
        launcher.launch(PickVisualMediaRequest())
    }
}
