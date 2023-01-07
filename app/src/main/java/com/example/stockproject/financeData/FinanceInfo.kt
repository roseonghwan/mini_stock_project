package com.example.stockproject.financeData

data class FinanceInfo(
    val list: List<Item>,
    val message: String,
    val status: String
)