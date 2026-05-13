package com.ayush.snap2sheet.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.ExpenseRepository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    suspend fun getAllExpensesForExport() = repository.getAllExpenses().first()
}
