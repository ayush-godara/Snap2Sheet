package com.ayush.snap2sheet.utils

import android.content.Context
import android.os.Environment
import com.ayush.snap2sheet.data.Expense
import java.io.File
import java.io.FileWriter

object CsvExporter {
    fun exportToCsv(context: Context, expenses: List<Expense>): String? {
        return try {
            val fileName = "ExpensesExport_${System.currentTimeMillis()}.csv"
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (dir != null && !dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, fileName)
            val writer = FileWriter(file)

            writer.append("ID,Merchant,Amount,Category,Date,Notes\n")
            for (expense in expenses) {
                writer.append("${expense.id},")
                writer.append("\"${expense.merchantName}\",")
                writer.append("${expense.amount},")
                writer.append("\"${expense.category}\",")
                writer.append("\"${expense.date}\",")
                writer.append("\"${expense.notes}\"\n")
            }
            writer.flush()
            writer.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
