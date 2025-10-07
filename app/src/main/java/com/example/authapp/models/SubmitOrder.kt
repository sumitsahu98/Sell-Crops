package com.example.authapp.repository

import com.example.authapp.models.Crop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

object OrderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun submitOrder(
        selectedItems: List<Crop>,
        buyerName: String,
        buyerAddress: String,
        buyerPhone: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (userId.isEmpty()) {
            onFailure("User not logged in")
            return
        }

        val orderId = db.collection("orders").document().id
        val timestamp = System.currentTimeMillis()

        val orderData = mapOf(
            "orderId" to orderId,
            "buyerId" to userId,
            "buyerName" to buyerName,
            "buyerAddress" to buyerAddress,
            "buyerPhone" to buyerPhone,
            "items" to selectedItems.map { crop ->
                mapOf(
                    "id" to crop.id,
                    "name" to crop.name,
                    "price" to crop.price,
                    "quantity" to crop.cartQuantity,
                    "sellerId" to crop.sellerId,
                    "imageUrl" to crop.imageUrl
                )
            },
            "totalAmount" to selectedItems.sumOf { crop ->
                val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
                val cartQty = crop.cartQuantity.toDoubleOrNull() ?: 0.0
                priceFor10Kg * cartQty / 10
            },
            "timestamp" to timestamp,
            "status" to "Pending" // ✅ Initial status
        )

        db.collection("orders").document(orderId).set(orderData)
            .addOnSuccessListener {
                updateCropQuantities(selectedItems, onSuccess, onFailure)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to submit order")
            }
    }

    private fun updateCropQuantities(
        selectedItems: List<Crop>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val batch = db.batch()

        try {
            selectedItems.forEach { crop ->
                val cropRef = db.collection("crops").document(crop.id)

                val availableQty = crop.quantity.toDoubleOrNull() ?: 0.0
                val purchasedQty = crop.cartQuantity.toDoubleOrNull() ?: 0.0
                val newQty = (availableQty - purchasedQty).coerceAtLeast(0.0)

                if (newQty > 0) {
                    batch.update(cropRef, "quantity", newQty.toString())
                } else {
                    batch.delete(cropRef)
                }
            }

            batch.commit()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.message ?: "Failed to update crop quantities") }

        } catch (e: Exception) {
            onFailure(e.message ?: "Unexpected error while updating crops")
        }
    }

    // ✅ Function to auto-update order status after certain time
    fun startOrderStatusUpdater(orderId: String) {
        val orderRef = db.collection("orders").document(orderId)
        // Delay times (in milliseconds) for demo
        val pendingDuration = 10_000L // 10 seconds
        val inTransitDuration = 15_000L // 15 seconds

        // Step 1: Pending → In Transit
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(pendingDuration)
            orderRef.update("status", "In Transit").addOnFailureListener { }
            // Step 2: In Transit → Delivered
            kotlinx.coroutines.delay(inTransitDuration)
            orderRef.update("status", "Delivered").addOnFailureListener { }
        }
    }
}
