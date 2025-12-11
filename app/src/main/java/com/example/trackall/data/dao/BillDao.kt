package com.example.trackall.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.trackall.data.entity.Bill
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills WHERE username = :username ORDER BY nextDueDate ASC")
    fun getBills(username: String): Flow<List<Bill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    @Update
    suspend fun updateBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)
    
    @Query("SELECT * FROM bills WHERE username = :username")
    suspend fun getBillsOneShot(username: String): List<Bill>
}
