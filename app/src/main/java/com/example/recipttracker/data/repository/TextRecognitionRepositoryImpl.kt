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

    override suspend fun recognizeTextFromImage(imageData: ByteArray): List<String> =
        suspendCancellableCoroutine { cont ->
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val priceRegex = Regex("""\$?\s?(?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d{2})$""")
                    val totalRegex = Regex("""\btotal\b""", RegexOption.IGNORE_CASE)

                    val allLinesWithY = visionText.textBlocks.flatMap { block ->
                        block.lines.mapNotNull { line ->
                            val yCenter = line.boundingBox?.centerY() ?: return@mapNotNull null
                            Triple(line.text.trim(), yCenter, line)
                        }
                    }.sortedBy { it.second } // Sort by Y value (top to bottom)

                    var firstPriceY: Int? = null
                    var totalLineY: Int? = null

                    // Find first price and total line Y values
                    for ((text, y, _) in allLinesWithY) {
                        if (firstPriceY == null && priceRegex.containsMatchIn(text)) {
                            firstPriceY = y
                        }
                        if (totalLineY == null && totalRegex.containsMatchIn(text)) {
                            totalLineY = y
                        }
                        if (firstPriceY != null && totalLineY != null) break
                    }

                    // Guard conditions
                    if (firstPriceY == null || totalLineY == null || totalLineY <= firstPriceY) {
                        cont.resume(emptyList()) // Nothing meaningful found
                        return@addOnSuccessListener
                    }

                    // Collect lines between firstPriceY and totalLineY
                    val collectedItems = allLinesWithY.filter { (text, y, _) ->
                        y > firstPriceY && y < totalLineY
                    }.map { it.first }

                    cont.resume(collectedItems)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
}
