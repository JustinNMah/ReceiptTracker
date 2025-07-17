package com.example.recipttracker.ui.capture

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipttracker.ui.theme.ReceiptTrackerTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.recipttracker.R

import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.recipttracker.data.repository.TextRecognitionRepositoryImpl

import kotlinx.coroutines.launch
@Composable
fun CaptureScreen() {
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repository = remember { TextRecognitionRepositoryImpl() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        if (recognizedText == null) {
            Image(
                painter = painterResource(id = R.drawable.costco_receipt),
                contentDescription = "Sample Receipt",
                modifier = Modifier.size(500.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val bitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.costco_receipt
                            )
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            val imageData = stream.toByteArray()

                            val result = repository.recognizeTextFromImage(imageData)
                            recognizedText = result.joinToString("\n")
                        } catch (e: Exception) {
                            recognizedText = "Error: ${e.localizedMessage}"
                        }
                    }
                },
                modifier = Modifier.width(185.dp)
            ) {
                Text("Process Receipt")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        recognizedText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CaptureScreenPreview() {
    ReceiptTrackerTheme {
        CaptureScreen()
    }
}

