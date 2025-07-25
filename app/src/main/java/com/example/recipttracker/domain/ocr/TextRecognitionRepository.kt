package com.example.recipttracker.domain.ocr

import com.example.recipttracker.data.repository.ExtractionResult

interface TextRecognitionRepository {
    suspend fun recognizeTextFromImage(filePath: String): ExtractionResult
}