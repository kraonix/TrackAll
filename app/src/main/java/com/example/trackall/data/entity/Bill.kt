package com.example.trackall.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val title: String,
    val amount: Double,
    val recurrenceMode: String, // ONE_TIME, MONTHLY, QUARTERLY, TRI_ANNUAL, YEARLY, CUSTOM
    val customInterval: Int = 0, // Used if recurrenceMode is CUSTOM
    val customIntervalUnit: String? = null, // "DAYS" or "MONTHS"
    val startDate: String, // format: "dd/MM/yyyy"
    val nextDueDate: String, // format: "dd/MM/yyyy"
    val lastAppliedDate: String = "", // format: "dd/MM/yyyy", empty if never applied
    val category: String,
    val notes: String = ""
)
