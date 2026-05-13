package com.ayush.snap2sheet.utils

import android.net.Uri
import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

object OCRHelper {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processImage(context: Context, imageUri: Uri): ReceiptData {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()
            val text = result.text
            extractData(text)
        } catch (e: Exception) {
            e.printStackTrace()
            ReceiptData("", 0.0, DateUtils.getCurrentDate())
        }
    }

    private fun extractData(text: String): ReceiptData {
        val lines = text.split("\n")
        var merchantName = ""
        var amount = 0.0
        var date = DateUtils.getCurrentDate()

        // Very basic heuristics
        if (lines.isNotEmpty()) {
            merchantName = lines[0] // Assume first line is merchant name
        }

        val amountRegex = Regex("(?i)(total|amount|sum).*?\\$?(\\d+\\.\\d{2})")
        val dateRegex = Regex("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})")

        for (line in lines) {
            if (amount == 0.0) {
                val amountMatch = amountRegex.find(line)
                if (amountMatch != null) {
                    amount = amountMatch.groupValues[2].toDoubleOrNull() ?: 0.0
                }
            }
            if (date == DateUtils.getCurrentDate()) {
                val dateMatch = dateRegex.find(line)
                if (dateMatch != null) {
                    date = dateMatch.groupValues[1]
                }
            }
        }

        // If specific keyword not found, look for largest floating number at the end
        if (amount == 0.0) {
            val allNumbers = Regex("\\d+\\.\\d{2}").findAll(text).map { it.value.toDoubleOrNull() ?: 0.0 }.toList()
            if (allNumbers.isNotEmpty()) {
                amount = allNumbers.maxOrNull() ?: 0.0
            }
        }

        return ReceiptData(merchantName, amount, date)
    }
}

data class ReceiptData(
    val merchantName: String,
    val amount: Double,
    val date: String
)
