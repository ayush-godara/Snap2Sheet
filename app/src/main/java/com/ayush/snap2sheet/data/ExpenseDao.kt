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

    @Query("SELECT * FROM expenses WHERE userId = :userId AND groupId = '' ORDER BY date DESC, id DESC")
    fun getAllExpenses(userId: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND groupId = '' AND merchantName LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchExpenses(userId: String, query: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND groupId = '' AND category = :category ORDER BY date DESC")
    fun getExpensesByCategory(userId: String, category: String): Flow<List<Expense>>

    @Query("SELECT category, SUM(amount) as total FROM expenses WHERE userId = :userId AND groupId = '' GROUP BY category")
    fun getCategoryTotals(userId: String): Flow<List<CategoryTotal>>

    @Query("SELECT substr(date, 1, 7) as month, SUM(amount) as total FROM expenses WHERE userId = :userId AND groupId = '' GROUP BY month ORDER BY month")
    fun getMonthlyTotals(userId: String): Flow<List<MonthlyTotal>>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND groupId = ''")
    fun getTotalExpense(userId: String): Flow<Double?>
    
    @Query("DELETE FROM expenses WHERE userId = :userId AND groupId = ''")
    suspend fun clearAll(userId: String)
}
