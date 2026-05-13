package com.ayush.snap2sheet

import android.app.Application
import androidx.room.Room
import com.ayush.snap2sheet.data.ExpenseDatabase
import com.ayush.snap2sheet.data.ExpenseRepository
import com.ayush.snap2sheet.data.FirestoreRepository

class Snap2SheetApp : Application() {
    lateinit var database: ExpenseDatabase
    lateinit var repository: ExpenseRepository
    lateinit var firestoreRepository: FirestoreRepository

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            ExpenseDatabase::class.java,
            "expense_db"
        ).fallbackToDestructiveMigration().build()
        repository = ExpenseRepository(database.expenseDao())
        firestoreRepository = FirestoreRepository()
    }
}
