package com.example.authapp.models

import com.example.authapp.models.Crop

data class Order(
    val orderId: String = "",
    val crop: Crop? = null,  // single crop for simplicity
    val totalAmount: Double = 0.0,
    val status: String? = "Pending",
    val timestamp: Long = System.currentTimeMillis()
)
