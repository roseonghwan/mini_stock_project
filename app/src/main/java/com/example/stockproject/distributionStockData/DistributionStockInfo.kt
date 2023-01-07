package com.example.stockproject.distributionStockData

data class DistributionStockInfo(
    val list: List<Item>,
    val message: String,
    val status: String
)