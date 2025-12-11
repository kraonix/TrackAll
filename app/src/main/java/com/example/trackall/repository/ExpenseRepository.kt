package com.example.trackall.repository

import androidx.lifecycle.LiveData
import com.example.trackall.data.dao.ExpenseDao
import com.example.trackall.data.entity.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Fetch expenses for a specific username
    fun getAllExpenses(username: String): LiveData<List<Expense>> {
        return expenseDao.getAllExpenses(username)
    }

    // Insert a new expense
    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    // Delete an expense
    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun getExpenseByBillIdAndDate(billId: Int, date: String): Expense? {
        return expenseDao.getExpenseByBillIdAndDate(billId, date)
    }

}
