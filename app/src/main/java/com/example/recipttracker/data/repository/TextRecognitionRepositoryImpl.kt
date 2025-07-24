package com.example.recipttracker.data.repository

import android.graphics.BitmapFactory
import com.example.recipttracker.domain.ocr.TextRecognitionRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ExtractionResult(
    val collectedItems: Set<String> = emptySet(),
    val total: String = "",
    val title: String = ""
)

class TextRecognitionRepositoryImpl : TextRecognitionRepository {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognizeTextFromImage(imageData: ByteArray): ExtractionResult =
        suspendCancellableCoroutine { cont ->
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                ?: return@suspendCancellableCoroutine cont.resumeWithException(
                        IllegalArgumentException("Invalid image data")
                        )
            val image = InputImage.fromBitmap(bitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (visionText.textBlocks.isEmpty()) {
                        cont.resume(ExtractionResult())
                        return@addOnSuccessListener
                    }

                    val priceRegex = Regex("""\$?\s*((?:\d{1,3}(?:,\d{3})+|\d+)(?:\.\d{2}))""")
                    val totalRegex = Regex("total", RegexOption.IGNORE_CASE)

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
                        if (totalRegex.containsMatchIn(text) && firstPriceY != null) {
                            totalLineY = y
                        }
                    }

                    val collectedItems = mutableSetOf<String>()
                    var total = ""

                    for ((text, y, _) in allLinesWithY) {
                        if ((firstPriceY == null || y > firstPriceY - 10) && (totalLineY == null || y < totalLineY + 10)){
                            if (priceRegex.containsMatchIn(text)) {
                                total = text
                            }
                            collectedItems.add(text)
                        }
                    }

                    val amountMatch = priceRegex.find(total)
                    val amountNumber = if (amountMatch != null) {
                        amountMatch.groups[1]?.value?.replace(",", "") ?: total
                    } else {
                        total
                    }


                    cont.resume(ExtractionResult(
                        collectedItems = collectedItems,
                        total = amountNumber,
                        title = allLinesWithY[0].first
                    ))
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
}
