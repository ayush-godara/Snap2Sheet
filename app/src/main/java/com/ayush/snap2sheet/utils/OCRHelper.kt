package com.ayush.snap2sheet.utils

import android.net.Uri
import android.content.Context
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

object OCRHelper {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Keyword dictionary for auto category detection
    private val categoryKeywords = mapOf(
        "Food" to listOf(
            "restaurant", "cafe", "coffee", "starbucks", "mcdonald", "mcdonalds", "pizza",
            "burger", "swiggy", "zomato", "zepto", "blinkit", "bigbasket", "grocery",
            "supermarket", "bakery", "food", "kitchen", "dine", "dining", "eat",
            "dominos", "kfc", "subway", "dunkin", "chai", "tea", "milk",
            "fruit", "vegetable", "meat", "chicken", "rice", "bread", "snack",
            "instamart", "jiomart", "dmart", "reliance fresh", "more", "walmart",
            "haldiram", "barbeque", "biryani", "thali", "canteen", "mess"
        ),
        "Shopping" to listOf(
            "amazon", "flipkart", "myntra", "ajio", "mall", "store",
            "retail", "clothing", "electronics", "meesho", "snapdeal",
            "nykaa", "lifestyle", "shoppers stop", "croma", "reliance digital",
            "fashion", "shoes", "watch", "jewel", "furniture", "decor",
            "ikea", "pepperfry", "firstcry", "bewakoof"
        ),
        "Travel" to listOf(
            "uber", "ola", "rapido", "irctc", "railway", "flight",
            "airport", "metro", "bus", "fuel", "petrol", "diesel",
            "parking", "toll", "cab", "taxi", "makemytrip", "goibibo",
            "redbus", "indigo", "spicejet", "vistara", "air india",
            "hotel", "oyo", "airbnb", "booking", "trivago"
        ),
        "Bills" to listOf(
            "electric", "electricity", "water", "gas", "internet", "wifi", "broadband",
            "jio", "airtel", "vi", "bsnl", "recharge", "postpaid", "prepaid",
            "rent", "maintenance", "society", "emi", "loan", "insurance",
            "subscription", "netflix", "spotify", "hotstar", "prime"
        ),
        "Health" to listOf(
            "hospital", "pharmacy", "medical", "doctor", "clinic",
            "medicine", "apollo", "1mg", "pharmeasy", "netmeds",
            "diagnostic", "lab", "test", "health", "dental", "eye",
            "gym", "fitness", "yoga", "wellness"
        ),
        "Education" to listOf(
            "book", "course", "udemy", "school", "college", "tuition",
            "stationery", "library", "exam", "coaching", "unacademy",
            "byju", "coursera", "edx", "pen", "notebook", "print"
        )
    )

    suspend fun processImage(context: Context, imageUri: Uri): ReceiptData {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()
            val text = result.text
            extractData(text)
        } catch (e: Exception) {
            e.printStackTrace()
            ReceiptData("", 0.0, DateUtils.getCurrentDate(), "Other")
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

        val amountRegex = Regex("(?i)(total|amount|sum|grand total|net amount|payable).*?\\$?₹?(\\d+\\.\\d{2})")
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

        // Auto-detect category
        val category = detectCategory(text, merchantName)

        return ReceiptData(merchantName, amount, date, category)
    }

    private fun detectCategory(fullText: String, merchantName: String): String {
        val searchText = (fullText + " " + merchantName).lowercase()

        // Score each category based on keyword matches
        var bestCategory = "Other"
        var bestScore = 0

        for ((category, keywords) in categoryKeywords) {
            var score = 0
            for (keyword in keywords) {
                if (searchText.contains(keyword)) {
                    score++
                }
            }
            if (score > bestScore) {
                bestScore = score
                bestCategory = category
            }
        }

        return bestCategory
    }
}

data class ReceiptData(
    val merchantName: String,
    val amount: Double,
    val date: String,
    val category: String = "Other"
)
