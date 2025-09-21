package com.example.authapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🔹 Crop Data Model
data class Crop(val name: String, val price: String, val quantity: String)

@Composable
fun CropItemBox(
    crop: Crop,
    onAddToCart: (Crop) -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Img", color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(crop.name, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(crop.price, fontSize = 12.sp, color = Color(0xFF388E3C), fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
            Text("Available: ${crop.quantity}", fontSize = 10.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddToCart(crop) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add to Cart", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}
