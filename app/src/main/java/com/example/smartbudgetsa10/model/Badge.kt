package com.example.smartbudgetsa10.model

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconResId: Int,
    val dateEarned: Long = System.currentTimeMillis()
)
