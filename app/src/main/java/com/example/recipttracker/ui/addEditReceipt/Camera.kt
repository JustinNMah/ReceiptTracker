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
    onFail: () -> Unit,
    modifyReceiptVM: ModifyReceiptVM
) {
    val file: File = File(LocalContext.current.getFilesDir(), "${System.currentTimeMillis()}")
    Log.d("Camera", "Creating filePath: ${file.absolutePath}")
    val uriPath: Uri = FileProvider.getUriForFile(
        LocalContext.current,
        BuildConfig.APPLICATION_ID + ".provider",
        file
    )

    val diffLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { status ->
        Log.d("Camera", "$status")
        if (status) {
            Log.d("Camera", "Stored image into filePath: ${file.absolutePath}")
            modifyReceiptVM.setReceiptToAdd(file.absolutePath)
            onFinish()
        } else {
            onFail()
        }
    }

    SideEffect { // this is a side effect because it modifies modifyReceiptVM state outside this composable
        diffLauncher.launch(uriPath)
    }
}
