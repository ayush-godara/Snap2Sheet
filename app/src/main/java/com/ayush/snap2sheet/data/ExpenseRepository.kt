package com.ayush.snap2sheet.data

import kotlinx.coroutines.flow.Flow


class ExpenseRepository(
    private val dao: ExpenseDao
) {
    suspend fun insertExpense(expense: Expense) = dao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = dao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = dao.deleteExpense(expense)
    suspend fun clearAll() = dao.clearAll()
    
    fun getAllExpenses(): Flow<List<Expense>> = dao.getAllExpenses()
    fun searchExpenses(query: String): Flow<List<Expense>> = dao.searchExpenses(query)
    fun getExpensesByCategory(category: String): Flow<List<Expense>> = dao.getExpensesByCategory(category)
    fun getCategoryTotals(): Flow<List<CategoryTotal>> = dao.getCategoryTotals()
    fun getMonthlyTotals(): Flow<List<MonthlyTotal>> = dao.getMonthlyTotals()
    fun getTotalExpense(): Flow<Double?> = dao.getTotalExpense()
}
