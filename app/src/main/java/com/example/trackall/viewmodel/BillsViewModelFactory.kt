package com.example.trackall.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackall.repository.BillRepository
import com.example.trackall.repository.ExpenseRepository

class BillsViewModelFactory(
    private val billRepository: BillRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillsViewModel(billRepository, expenseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
