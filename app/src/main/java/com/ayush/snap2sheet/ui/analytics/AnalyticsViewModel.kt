package com.ayush.snap2sheet.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.CategoryTotal
import com.ayush.snap2sheet.data.ExpenseRepository
import com.ayush.snap2sheet.data.MonthlyTotal

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(
    repository: ExpenseRepository
) : ViewModel() {

    val totalExpense: StateFlow<Double?> = repository.getTotalExpense()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val categoryTotals: StateFlow<List<CategoryTotal>> = repository.getCategoryTotals()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val monthlyTotals: StateFlow<List<MonthlyTotal>> = repository.getMonthlyTotals()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
