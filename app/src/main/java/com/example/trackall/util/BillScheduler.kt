package com.example.trackall.util

import com.example.trackall.data.entity.Bill
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object BillScheduler {

    data class BillState(
        val updatedBill: Bill,
        val expensesToGenerate: List<String>
    )

    fun calculateBillState(bill: Bill): BillState {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayCalendar = Calendar.getInstance()
        clearTime(todayCalendar)
        val todayDate = todayCalendar.time
        val todayStr = dateFormat.format(todayDate)

        val startDate = dateFormat.parse(bill.startDate) ?: return BillState(bill, emptyList())
        val expensesToGenerate = mutableListOf<String>()
        var nextDueDateRaw = startDate
        var lastApplied = bill.lastAppliedDate

        // 1. Start Date Rule: Always generate if Today or Past
        if (startDate.compareTo(todayDate) <= 0) {
            val startStr = dateFormat.format(startDate)
            expensesToGenerate.add(startStr)
            lastApplied = startStr
        }

        // 2. Recurrence Logic
        if (bill.recurrenceMode != "ONE_TIME") {
            // Start from start date
            var loopDate = startDate
            
            // If start date is past/today, we iterate to find the true Next Due Date
            if (startDate.compareTo(todayDate) <= 0) {
                 // First step: move away from start date (already handled)
                 val tempBill = bill.copy() 
                 loopDate = calculateNextDueDate(tempBill, loopDate)
                 
                 // Fast-Forward: Skip strictly past dates
                 while (loopDate.before(todayDate)) {
                     loopDate = calculateNextDueDate(tempBill, loopDate)
                 }
                 
                 // Now loopDate >= Today. 
                 // If it equals Today, generate expense.
                 if (loopDate.compareTo(todayDate) == 0) {
                     val dateStr = dateFormat.format(loopDate)
                     // Avoid diff format/string dupes
                     if (!expensesToGenerate.contains(dateStr)) {
                         expensesToGenerate.add(dateStr)
                         lastApplied = dateStr
                     }
                     // Current interval paid, move to next
                     // Logic: If we paid today, the NEXT due is future
                     loopDate = calculateNextDueDate(tempBill, loopDate)
                 }
            }
            
            nextDueDateRaw = loopDate
        } 
        
        val nextDueStr = if (bill.recurrenceMode == "ONE_TIME" && startDate.compareTo(todayDate) <= 0) {
            "" // Done
        } else {
             dateFormat.format(nextDueDateRaw)
        }
        
        return BillState(bill.copy(nextDueDate = nextDueStr, lastAppliedDate = lastApplied), expensesToGenerate)
    }

    // Single source of truth for date calculation
    fun calculateNextDueDate(bill: Bill, currentDueDate: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = currentDueDate
        
        when (bill.recurrenceMode) {
            "MONTHLY" -> cal.add(Calendar.MONTH, 1)
            "QUARTERLY" -> cal.add(Calendar.MONTH, 3) 
            "TRI_ANNUAL" -> cal.add(Calendar.MONTH, 4) 
            "EVERY_3_MONTHS" -> cal.add(Calendar.MONTH, 3)
            "EVERY_9_MONTHS" -> cal.add(Calendar.MONTH, 9)
            "YEARLY" -> cal.add(Calendar.YEAR, 1)
            "CUSTOM" -> {
                if (bill.customIntervalUnit == "MONTHS") {
                    cal.add(Calendar.MONTH, bill.customInterval)
                } else {
                    cal.add(Calendar.DAY_OF_YEAR, bill.customInterval)
                }
            }
        }
        return cal.time
    }

    private fun clearTime(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
}
