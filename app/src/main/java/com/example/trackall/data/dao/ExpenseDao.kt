package com.example.trackall.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.trackall.data.entity.Expense

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE username = :username ORDER BY date DESC")
    fun getAllExpenses(username: String): LiveData<List<Expense>>

    @Update
    suspend fun updateExpense(expense: Expense)

}
