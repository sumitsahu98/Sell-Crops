package com.example.authapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.ui.components.DefaultTopBar

// ==========================
// Orders Screen
// ==========================
@Composable
fun OrdersScreen(navController: NavController) {
    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "My Orders",
                onBackClick = { navController.navigateUp() }
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = "orders") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔹 Orders List
            val orders = listOf(
                Order("Onion", "₹15/kg", "200kg", "Delivered"),
                Order("Tomato", "₹20/kg", "150kg", "In Transit"),
                Order("Wheat", "₹25/kg", "500kg", "Cancelled"),
                Order("Rice", "₹30/kg", "300kg", "Delivered"),
                Order("Potato", "₹12/kg", "400kg", "Pending")
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Image placeholder
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.LightGray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Img")
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Text(order.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(order.price, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                                Text("Quantity: ${order.quantity}", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "Status: ${order.status}",
                                    fontSize = 12.sp,
                                    color = when (order.status) {
                                        "Delivered" -> Color(0xFF388E3C)
                                        "Cancelled" -> Color.Red
                                        "Pending" -> Color(0xFFFFA000)
                                        else -> Color.Gray
                                    }
                                )
                            }

                            Button(
                                onClick = { /* Navigate to order details or track */ },
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("View")
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🔹 Order Data Model
data class Order(
    val name: String,
    val price: String,
    val quantity: String,
    val status: String
)
