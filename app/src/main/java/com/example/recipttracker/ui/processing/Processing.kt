package com.example.recipttracker.ui.capture

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import java.io.ByteArrayOutputStream

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.recipttracker.data.repository.TextRecognitionRepositoryImpl
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptVM

import kotlinx.coroutines.launch
@Composable
fun Processing(
    onFinish: () -> Unit,
    onFail: () -> Unit,
    modifyReceiptVM: ModifyReceiptVM
) {
    val scope = rememberCoroutineScope()
    val repository = remember { TextRecognitionRepositoryImpl() }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val bitmap = BitmapFactory.decodeFile(
                    modifyReceiptVM.filePath.value
                )
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageData = stream.toByteArray()

                val result = repository.recognizeTextFromImage(imageData)
                modifyReceiptVM.editReceiptToAdd(result.title, result.total, result.collectedItems)
                onFinish()
            } catch (e: Exception) {
                onFail()
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
