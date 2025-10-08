package com.example.authapp.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.authapp.models.Crop
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailsScreen(navController: NavController, cropJson: String? = null) {

    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val crop = try {
        Gson().fromJson(cropJson, Crop::class.java)
    } catch (e: Exception) {
        null
    }

    if (crop == null) {
        Toast.makeText(context, "Error loading crop details", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
        return
    }

    // Calculate price for 10 kg
    val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0

    // Check if the current user is the seller
    val isSelf = currentUserId == crop.sellerId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(crop.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Crop Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!crop.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = crop.imageUrl,
                        contentDescription = crop.name.ifBlank { "Crop Image" },
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Text("Crop Image", color = Color.DarkGray)
                }
            }

            // Crop Details
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Price: ₹${crop.price} / kg",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF388E3C)
                )
                Text(
                    "Price for 10kg: ₹${priceFor10Kg.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF388E3C)
                )
                Text("Available Quantity: ${crop.quantity} kg", fontSize = 16.sp)
                Text("Category: ${crop.category}", fontSize = 16.sp)
                Text("Location: ${crop.location}", fontSize = 16.sp)
                Text("Delivery Date: ${crop.deliveryDate}", fontSize = 16.sp)

                if (crop.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Description:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(crop.description, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isSelf) {
                        Toast.makeText(
                            context,
                            "You cannot message yourself",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        navController.navigate("chat/${crop.sellerId}")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelf) Color.Gray else Color(0xFF4CAF50)
                ),
                enabled = !isSelf
            ) {
                Text(
                    text = if (isSelf) "Cannot Contact Yourself" else "Contact Seller",
                    color = Color.White
                )
            }
        }
    }
}
