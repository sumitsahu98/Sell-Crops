package com.example.authapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    val cartItems = mutableStateListOf<Crop>()

    fun addToCart(crop: Crop) {
        cartItems.add(crop)
    }

    fun removeFromCart(crop: Crop) {
        cartItems.remove(crop)
    }

    fun clearCart() {
        cartItems.clear()
    }
}
