package com.example.authapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.models.Crop
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CropDetailsScreen(cropId: String) {
    var crop by remember { mutableStateOf<Crop?>(null) }

    // Fetch crop from Firestore
    LaunchedEffect(cropId) {
        FirebaseFirestore.getInstance()
            .collection("crops")
            .document(cropId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    crop = Crop(
                        id = doc.id,
                        name = doc.getString("cropName") ?: "",
                        price = doc.getString("cropPrice") ?: "",
                        quantity = doc.getString("cropQuantity") ?: ""
                    )
                }
            }
    }

    if (crop == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Img")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(crop!!.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(crop!!.price, fontSize = 18.sp, color = Color(0xFF388E3C))
            Text("Available: ${crop!!.quantity}", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { /* TODO: Add to cart / buy */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Buy Now", color = Color.White)
            }
        }
    }
}
