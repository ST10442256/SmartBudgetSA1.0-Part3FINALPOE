package com.example.smartbudgetsa10.model

import java.util.Date

data class Expense(
    val id: String,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Long = System.currentTimeMillis()
)
