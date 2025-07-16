package com.example.recipttracker.ui.addEditReceipt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
fun CameraRoll(
    onFinish: () -> Unit,
    onFail: () -> Unit,
    modifyReceiptVM: ModifyReceiptVM
) {
    val localContext = LocalContext.current

    val handleClick: (Uri) -> Unit = { imgUri ->
        Log.d("CameraRoll", "Uri path for photo: $imgUri")

        val inputStream = localContext.contentResolver.openInputStream(imgUri)
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        val file: File = File(localContext.getFilesDir(), "${System.currentTimeMillis()}")

        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        Log.d("CameraRoll", "Saved image to ${file.absolutePath}")

        modifyReceiptVM.setReceiptToAdd(file.absolutePath)
        onFinish()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            handleClick(uri)
        } else {
            Log.d("CameraRoll", "Did not choose photo")
            onFail()
        }
    }

    SideEffect {
        launcher.launch(PickVisualMediaRequest())
    }
}
