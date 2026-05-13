package com.ayush.snap2sheet.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.CategoryTotal
import com.ayush.snap2sheet.data.ExpenseRepository
import com.ayush.snap2sheet.data.MonthlyTotal
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(
    repository: ExpenseRepository
) : ViewModel() {

    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val totalExpense: StateFlow<Double?> = repository.getTotalExpense(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val categoryTotals: StateFlow<List<CategoryTotal>> = repository.getCategoryTotals(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val monthlyTotals: StateFlow<List<MonthlyTotal>> = repository.getMonthlyTotals(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
