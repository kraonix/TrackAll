package com.example.trackall.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val amount: Double,
    val description: String,
    val date: String, // format: "dd/MM/yyyy"
    val category: String
)
