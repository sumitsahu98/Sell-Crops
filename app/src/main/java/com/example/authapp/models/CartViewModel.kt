package com.example.authapp.models

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModel : ViewModel() {

    val cartItems = mutableStateListOf<Crop>()
    private val stepKg = 10
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadCartFromFirestore()
    }

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
        saveCartToFirestore()
    }

    // Decrease cart quantity
    fun decreaseCartQuantity(crop: Crop) {
        val existing = cartItems.find { it.id == crop.id } ?: return
        val index = cartItems.indexOf(existing)
        val currentCartQty = existing.cartQuantity.toIntOrNull() ?: 0
        if (currentCartQty > stepKg) {
            cartItems[index] = existing.copy(cartQuantity = (currentCartQty - stepKg).toString())
        } else {
            cartItems.removeAt(index)
        }
        saveCartToFirestore()
    }

    // Calculate total price
    fun getTotal(): Int {
        return cartItems.sumOf { crop ->
            val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
            val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
            (priceFor10Kg * cartQty / 10).toInt()
        }
    }

    // Clear cart
    fun clearCart() {
        cartItems.clear()
        saveCartToFirestore()
    }

    // Save cart to Firestore
    fun saveCartToFirestore() {
        if (userId.isEmpty()) return

        val cartMap = cartItems.map { crop ->
            mapOf(
                "id" to crop.id,
                "name" to crop.name,
                "price" to crop.price,
                "quantity" to crop.quantity,
                "cartQuantity" to crop.cartQuantity,
                "category" to crop.category,
                "description" to crop.description,
                "deliveryDate" to crop.deliveryDate,
                "location" to crop.location,
                "sellerId" to crop.sellerId,
                "imageUrl" to crop.imageUrl
            )
        }

        db.collection("carts").document(userId).set(mapOf("items" to cartMap))
    }

    // Load cart from Firestore
    fun loadCartFromFirestore(onLoaded: () -> Unit = {}) {
        if (userId.isEmpty()) return

        db.collection("carts").document(userId).get()
            .addOnSuccessListener { doc ->
                cartItems.clear()
                val items = doc.get("items") as? List<Map<String, Any>>
                items?.forEach { map ->
                    cartItems.add(
                        Crop(
                            id = map["id"] as? String ?: "",
                            name = map["name"] as? String ?: "",
                            price = map["price"] as? String ?: "",
                            quantity = map["quantity"] as? String ?: "",
                            cartQuantity = map["cartQuantity"] as? String ?: "0",
                            category = map["category"] as? String ?: "",
                            description = map["description"] as? String ?: "",
                            deliveryDate = map["deliveryDate"] as? String ?: "",
                            location = map["location"] as? String ?: "",
                            sellerId = map["sellerId"] as? String ?: "",
                            imageUrl = map["imageUrl"] as? String
                        )
                    )
                }
                onLoaded()
            }
    }
}
