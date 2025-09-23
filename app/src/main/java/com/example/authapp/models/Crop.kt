package com.example.authapp.models

data class Crop(
    val id: String = "",                // Firestore document ID
    val name: String = "",              // Crop name
    val price: String = "",             // Price per kg
    val quantity: String = "",          // Quantity in kg
    val cartQuantity: String= "",
    val category: String = "",          // Crop category
    val description: String = "",       // Optional description
    val deliveryDate: String = "",      // Expected delivery date
    val location: String = "",          // Crop location
    val sellerId: String = "",          // UID of the seller
    val timestamp: Long = System.currentTimeMillis()  // Listing timestamp
)
