package com.example.trackall.repository

import com.example.trackall.data.dao.BillDao
import com.example.trackall.data.entity.Bill
import kotlinx.coroutines.flow.Flow

class BillRepository(private val billDao: BillDao) {

    fun getBills(username: String): Flow<List<Bill>> {
        return billDao.getBills(username)
    }

    suspend fun insert(bill: Bill): Long {
        return billDao.insertBill(bill)
    }

    suspend fun update(bill: Bill) {
        billDao.updateBill(bill)
    }

    suspend fun delete(bill: Bill) {
        billDao.deleteBill(bill)
    }
    
    suspend fun getBillsOneShot(username: String): List<Bill> {
        return billDao.getBillsOneShot(username)
    }
}
