package com.ayush.snap2sheet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ayush.snap2sheet.databinding.ActivityMainBinding
import com.ayush.snap2sheet.data.ExpenseRepository
import com.ayush.snap2sheet.data.Expense
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var repository: ExpenseRepository

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        repository = (application as Snap2SheetApp).repository

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)

        checkAndAddSampleData()
    }

    private fun checkAndAddSampleData() {
        lifecycleScope.launch {
            val total = repository.getTotalExpense().first()
            if (total == null || total == 0.0) {
                val samples = listOf(
                    Expense(merchantName = "Starbucks", amount = 12.50, category = "Food", date = "2023-10-01"),
                    Expense(merchantName = "Uber", amount = 25.00, category = "Travel", date = "2023-10-02"),
                    Expense(merchantName = "Amazon", amount = 150.00, category = "Shopping", date = "2023-10-03"),
                    Expense(merchantName = "Walmart", amount = 85.20, category = "Food", date = "2023-10-04"),
                    Expense(merchantName = "Electric Bill", amount = 120.00, category = "Bills", date = "2023-10-05")
                )
                samples.forEach { repository.insertExpense(it) }
            }
        }
    }
}
