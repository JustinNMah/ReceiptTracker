package com.example.recipttracker.ui.addEditReceipt

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.recipttracker.BuildConfig
import java.io.File
import androidx.compose.runtime.SideEffect

@Composable
fun Camera(
    onFinish: () -> Unit,
    receiptToEditOrAdd: ReceiptToEditOrAdd
) {
    val diffLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        Log.d("TAG", "$uri")
        onFinish()
    }

    val filePath: File = File(LocalContext.current.getFilesDir(), "${System.currentTimeMillis()}")
    Log.d("TAG", "Creating filePath: $filePath")
    val uriPath: Uri = FileProvider.getUriForFile(
        LocalContext.current,
        BuildConfig.APPLICATION_ID + ".provider",
        filePath
    )

    SideEffect {
        diffLauncher.launch(uriPath)
    }
    Log.d("TAG", "Stored image into uriPath: ${uriPath}")
    Log.d("TAG", "toString(): ${uriPath.toString()}")
    receiptToEditOrAdd.changeUriPath(uriPath.toString())
}
