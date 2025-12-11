package com.example.trackall.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.trackall.data.entity.Bill
import com.example.trackall.data.entity.Expense
import com.example.trackall.repository.BillRepository
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.util.BillScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BillsViewModel(
    private val billRepository: BillRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    fun getBills(username: String): LiveData<List<Bill>> {
        return billRepository.getBills(username).asLiveData()
    }

    fun addBill(bill: Bill) {
        viewModelScope.launch {
            // CORE LOGIC DELEGATION
            val state = BillScheduler.calculateBillState(bill)
            
            // 1. Insert Final State Bill
            val newBillId = billRepository.insert(state.updatedBill).toInt()
            
            // 2. Generate Expenses (using real ID)
            state.expensesToGenerate.forEach { dateStr ->
                // STRICT DUPLICATE CHECK: billId + date
                if (expenseRepository.getExpenseByBillIdAndDate(newBillId, dateStr) == null) {
                    val billWithId = state.updatedBill.copy(id = newBillId)
                    createExpenseFromBill(billWithId, dateStr)
                }
            }
        }
    }
    
    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            billRepository.delete(bill)
        }
    }
    
    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            // Full Recalculation on Update
            val state = BillScheduler.calculateBillState(bill)
            val finalBill = state.updatedBill
            
            billRepository.update(finalBill)
            
            // Process any immediate expenses
            state.expensesToGenerate.forEach { dateStr ->
                if (expenseRepository.getExpenseByBillIdAndDate(finalBill.id, dateStr) == null) {
                    createExpenseFromBill(finalBill, dateStr)
                }
            }
        }
    }

    fun checkAndGenerateExpenses(username: String) {
        viewModelScope.launch {
            val bills = billRepository.getBillsOneShot(username)
            bills.forEach { bill ->
               // Reuse the single logic source for daily checks too
               // Recalculating state from the *current* bill object should naturally advance it if needed
               // But calculating from Start Date (which calculateBillState does) is safer against missed gaps.
               val state = BillScheduler.calculateBillState(bill)
               
               // If state changed (new Due Date or Last Applied), update DB
               if (state.updatedBill.nextDueDate != bill.nextDueDate || state.updatedBill.lastAppliedDate != bill.lastAppliedDate) {
                   billRepository.update(state.updatedBill)
               }
               
               state.expensesToGenerate.forEach { dateStr ->
                   if (expenseRepository.getExpenseByBillIdAndDate(bill.id, dateStr) == null) {
                       createExpenseFromBill(state.updatedBill.copy(id = bill.id), dateStr)
                   }
               }
            }
        }
    }
    
    private suspend fun createExpenseFromBill(bill: Bill, dateStr: String) {
        val expense = Expense(
            username = bill.username,
            amount = bill.amount,
            description = bill.title,
            date = dateStr,
            category = bill.category,
            billId = bill.id // Valid Link
        )
        expenseRepository.insert(expense)
    }

    // Local Helper Removed -> Delegated to BillScheduler
}
