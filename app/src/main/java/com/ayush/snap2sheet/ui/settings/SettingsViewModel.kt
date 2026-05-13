package com.ayush.snap2sheet.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll(userId)
        }
    }

    suspend fun getAllExpensesForExport() = repository.getAllExpenses(userId).first()
}
