package com.ayush.snap2sheet.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.Expense
import com.ayush.snap2sheet.data.ExpenseRepository
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpensesViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val expenses: StateFlow<List<Expense>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllExpenses(userId)
            } else {
                repository.searchExpenses(userId, query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}
