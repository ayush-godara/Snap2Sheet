package com.ayush.snap2sheet

import android.app.Application
import androidx.room.Room
import com.ayush.snap2sheet.data.ExpenseDatabase
import com.ayush.snap2sheet.data.ExpenseRepository

class Snap2SheetApp : Application() {
    lateinit var database: ExpenseDatabase
    lateinit var repository: ExpenseRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            ExpenseDatabase::class.java,
            "expense_db"
        ).build()
        repository = ExpenseRepository(database.expenseDao())
    }
}
