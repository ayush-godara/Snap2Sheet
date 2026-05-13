package com.ayush.snap2sheet.ui.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayush.snap2sheet.data.Expense
import com.ayush.snap2sheet.data.ExpenseRepository
import com.ayush.snap2sheet.utils.OCRHelper
import com.ayush.snap2sheet.utils.ReceiptData
import kotlinx.coroutines.launch

class ScanViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _receiptData = MutableLiveData<ReceiptData>()
    val receiptData: LiveData<ReceiptData> = _receiptData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    fun processImage(context: android.content.Context, uri: android.net.Uri) {
        _isLoading.value = true
        viewModelScope.launch {
            val data = OCRHelper.processImage(context, uri)
            _receiptData.postValue(data)
            _isLoading.postValue(false)
        }
    }

    fun saveExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
            _saveStatus.postValue(true)
        }
    }
}
