package com.example.recipttracker.data.repository

import android.graphics.BitmapFactory
import com.example.recipttracker.domain.ocr.TextRecognitionRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TextRecognitionRepositoryImpl : TextRecognitionRepository {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognizeTextFromImage(imageData: ByteArray): String =
        suspendCancellableCoroutine { cont ->
            // Convert ByteArray to Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    cont.resume(visionText.text)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
}
