package com.example.recipttracker.domain.ocr

interface TextRecognitionRepository {
    suspend fun recognizeTextFromImage(imageData: ByteArray): String
}