package com.example.recipttracker.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

@Composable
fun Camera(onFinish: (String) -> Unit) {
    val context = LocalContext.current

    val onImageCaptured: (Bitmap) -> Unit = { bitmap ->
        val filename = "captured_receipt_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Log.d("TAG", "Saving image to ${file.absolutePath}")
        val encodedPath = Uri.encode(file.absolutePath)
        onFinish(encodedPath)
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onImageCaptured(bitmap)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takePictureLauncher.launch(null)
        }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        LaunchedEffect(Unit) {
            takePictureLauncher.launch(null)
        }
    } else {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
