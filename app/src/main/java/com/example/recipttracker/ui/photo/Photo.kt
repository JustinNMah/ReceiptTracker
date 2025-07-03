package com.example.recipttracker.ui.photo

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import java.io.File
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun Photo(filePath: String, onFinish: () -> Unit) {
    val imgFile = File(filePath)

    if (imgFile.exists()) {
        val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Receipt photo",
            )
            Text(
                text = "Absolute file path: $filePath",
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        Log.d("TAG", "Image $filePath does not exist")
    }
}
