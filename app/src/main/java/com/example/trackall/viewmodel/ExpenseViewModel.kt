package com.example.trackall.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackall.data.entity.Expense
import com.example.trackall.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    fun getExpenses(username: String): LiveData<List<Expense>> {
        return repository.getAllExpenses(username)
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insert(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }


    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.update(expense)
        }
    }
}
