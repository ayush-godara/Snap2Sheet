package com.ayush.snap2sheet.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayush.snap2sheet.data.ExpenseRepository
import com.ayush.snap2sheet.data.FirestoreRepository
import com.ayush.snap2sheet.ui.analytics.AnalyticsViewModel
import com.ayush.snap2sheet.ui.expenses.ExpensesViewModel
import com.ayush.snap2sheet.ui.scan.ScanViewModel
import com.ayush.snap2sheet.ui.settings.SettingsViewModel
import com.ayush.snap2sheet.ui.groups.GroupViewModel

class ViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScanViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ExpensesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpensesViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AnalyticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalyticsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GroupViewModelFactory(private val firestoreRepository: FirestoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
