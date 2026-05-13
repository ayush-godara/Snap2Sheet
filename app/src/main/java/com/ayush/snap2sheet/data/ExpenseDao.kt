package com.ayush.snap2sheet.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE merchantName LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchExpenses(query: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    fun getCategoryTotals(): Flow<List<CategoryTotal>>

    @Query("SELECT substr(date, 1, 7) as month, SUM(amount) as total FROM expenses GROUP BY month ORDER BY month")
    fun getMonthlyTotals(): Flow<List<MonthlyTotal>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Flow<Double?>
    
    @Query("DELETE FROM expenses")
    suspend fun clearAll()
}


