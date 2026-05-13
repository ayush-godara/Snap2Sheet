package com.ayush.snap2sheet.data

import kotlinx.coroutines.flow.Flow


class ExpenseRepository(
    private val dao: ExpenseDao
) {
    suspend fun insertExpense(expense: Expense) = dao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = dao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = dao.deleteExpense(expense)
    suspend fun clearAll(userId: String) = dao.clearAll(userId)
    
    fun getAllExpenses(userId: String): Flow<List<Expense>> = dao.getAllExpenses(userId)
    fun searchExpenses(userId: String, query: String): Flow<List<Expense>> = dao.searchExpenses(userId, query)
    fun getExpensesByCategory(userId: String, category: String): Flow<List<Expense>> = dao.getExpensesByCategory(userId, category)
    fun getCategoryTotals(userId: String): Flow<List<CategoryTotal>> = dao.getCategoryTotals(userId)
    fun getMonthlyTotals(userId: String): Flow<List<MonthlyTotal>> = dao.getMonthlyTotals(userId)
    fun getTotalExpense(userId: String): Flow<Double?> = dao.getTotalExpense(userId)
}
