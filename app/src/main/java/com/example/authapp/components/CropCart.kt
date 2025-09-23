package com.example.authapp.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.google.gson.Gson

@Composable
fun CropCard(
    crop: Crop,
    cartViewModel: CartViewModel,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                // Convert crop to JSON and URL-encode
                val cropJson = Uri.encode(Gson().toJson(crop))
                navController.navigate("details/$cropJson")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFF9F9F9))
                .padding(16.dp)
                .heightIn(min = 220.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = crop.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Crop Image",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(54.dp)
                        .padding(top = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (crop.category.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFDCE775),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = crop.category,
                        fontSize = 12.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        color = Color(0xFF33691E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
            Text(
                text = "₹ ${priceFor10Kg.toInt()}/10kg",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFF388E3C)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${crop.quantity} kg available",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            AddToCartButton(
                crop = crop,
                cartViewModel = cartViewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
