package com.ayush.snap2sheet.data

data class GroupExpense(
    val id: String = "",
    val groupId: String = "",
    val addedBy: String = "",
    val addedByName: String = "",
    val merchantName: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: String = "",
    val imagePath: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
