package com.example.authapp.models

data class CartItem(
    val cropId: String = "",      // ID of the crop
    val name: String = "",        // Crop name
    val pricePerKg: Double = 0.0,
    val quantity: Int = 0,        // Quantity added to cart
    val totalPrice: Double = 0.0,
    val imageUrl: String? = null
)
