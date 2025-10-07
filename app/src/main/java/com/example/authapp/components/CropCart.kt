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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
                val cropJson = Uri.encode(Gson().toJson(crop))
                navController.navigate("details/$cropJson")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFF9F9F9))
                .padding(16.dp)
        ) {
            // Crop Image (90% width)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!crop.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = crop.imageUrl,
                        contentDescription = crop.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Crop Image",
                        tint = Color.Gray,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Crop name below image
            Text(
                text = crop.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 1,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category badge
            if (crop.category.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFDCE775),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = crop.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF33691E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
            Text(
                text = "₹ ${priceFor10Kg.toInt()}/10kg",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF388E3C),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))
            val avialable= (crop.quantity.toIntOrNull() ?: 0)
            Text(
                text = "${avialable} kg available",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
