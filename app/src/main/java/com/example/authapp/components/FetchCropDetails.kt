package com.example.authapp.repository

import com.example.authapp.models.Crop
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow

object CropRepository {

    private val db = FirebaseFirestore.getInstance()

    // Fetch all crops as a Flow
    fun getAllCrops(): Flow<List<Crop>> = callbackFlow {
        val listener = db.collection("crops")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e) // close flow on error
                    return@addSnapshotListener
                }
                val crops = snapshot?.documents?.map { doc ->
                    Crop(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        price = doc.getString("price") ?: "",
                        quantity = doc.getString("quantity") ?: "",
                        category = doc.getString("category") ?: "",
                        description = doc.getString("description") ?: "",
                        deliveryDate = doc.getString("deliveryDate") ?: "",
                        location = doc.getString("location") ?: "",
                        sellerId = doc.getString("sellerId") ?: "",
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(crops)
            }

        awaitClose { listener.remove() }
    }
}
