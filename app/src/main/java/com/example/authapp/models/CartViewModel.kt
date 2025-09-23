package com.example.authapp.models

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
class CartViewModel : ViewModel() {

    val cartItems = mutableStateListOf<Crop>()
    private val stepKg = 10

    // Add to cart (increments cartQuantity by 10kg)
    fun addToCart(crop: Crop) {
        val existing = cartItems.find { it.id == crop.id }
        if (existing == null) {
            cartItems.add(crop.copy(cartQuantity = stepKg.toString()))
        } else {
            val index = cartItems.indexOf(existing)
            val currentCartQty = existing.cartQuantity.toIntOrNull() ?: 0
            cartItems[index] = existing.copy(cartQuantity = (currentCartQty + stepKg).toString())
        }
    }

    // Decrease cartQuantity
    fun decreaseCartQuantity(crop: Crop) {
        val existing = cartItems.find { it.id == crop.id } ?: return
        val index = cartItems.indexOf(existing)
        val currentCartQty = existing.cartQuantity.toIntOrNull() ?: 0
        if (currentCartQty > stepKg) {
            cartItems[index] = existing.copy(cartQuantity = (currentCartQty - stepKg).toString())
        } else {
            cartItems.removeAt(index)
        }
    }

    // Calculate total price
    fun getTotal(): Int {
        return cartItems.sumOf { crop ->
            val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
            val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
            (priceFor10Kg * cartQty / 10).toInt()
        }
    }

    fun clearCart() = cartItems.clear()
}
