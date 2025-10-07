package com.example.authapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.models.Crop
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var orders by remember { mutableStateOf(listOf<OrderData>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch orders from Firestore
    LaunchedEffect(Unit) {
        db.collection("orders")
            .whereEqualTo("buyerId", userId)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (error != null) return@addSnapshotListener

                orders = snapshot?.documents?.map { doc ->
                    val items = (doc.get("items") as? List<Map<String, Any>>)?.map { item ->
                        Crop(
                            id = item["id"] as? String ?: "",
                            name = item["name"] as? String ?: "",
                            price = item["price"] as? String ?: "",
                            cartQuantity = item["quantity"] as? String ?: "",
                            sellerId = item["sellerId"] as? String ?: "",
                            imageUrl = item["imageUrl"] as? String ?: ""
                        )
                    } ?: emptyList()

                    OrderData(
                        orderId = doc.getString("orderId") ?: "",
                        items = items,
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                        status = doc.getString("status") ?: "Pending",
                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    )
                }?.sortedByDescending { it.timestamp } // 🔹 Latest orders first
                    ?: emptyList()
            }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "My Orders",
                onBackClick = { navController.navigateUp() }
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = "orders") }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                orders.isEmpty() -> Text(
                    "No orders yet",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp
                )

                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                val dateFormat =
                                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                val orderDate = dateFormat.format(Date(order.timestamp))

                                Text("Order ID: ${order.orderId}", fontWeight = FontWeight.Bold)
                                Text(
                                    "Date: $orderDate",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Crop items list
                                order.items.forEach { crop ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(crop.imageUrl),
                                            contentDescription = crop.name,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(Color.LightGray)
                                                .padding(4.dp)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(crop.name, fontWeight = FontWeight.Bold)
                                            val priceFor10Kg =
                                                crop.price.toDoubleOrNull()?.times(10) ?: 0.0
                                            val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
                                            Text(
                                                "₹${(priceFor10Kg * cartQty / 10).toInt()} (${cartQty}kg)",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Total: ₹${order.totalAmount}", fontWeight = FontWeight.Bold)

                                // Status text with color
                                val statusColor = when (order.status) {
                                    "Delivered" -> Color(0xFF388E3C)
                                    "Shipped", "In Transit" -> Color(0xFF1976D2)
                                    "Cancelled" -> Color.Red
                                    "Pending" -> Color(0xFFFFA000)
                                    else -> Color.Gray
                                }
                                Text("Status: ${order.status}", color = statusColor)

                                Spacer(modifier = Modifier.height(8.dp))

                                // Progress bar
                                val progress = when (order.status) {
                                    "Pending" -> 0.33f
                                    "Shipped", "In Transit" -> 0.66f
                                    "Delivered" -> 1f
                                    "Cancelled" -> 0.5f
                                    else -> 0f
                                }

                                val progressColor = when (order.status) {
                                    "Delivered" -> Color(0xFF388E3C)
                                    "Shipped", "In Transit" -> Color(0xFF1976D2)
                                    "Cancelled" -> Color.Red
                                    else -> Color(0xFFFFA000)
                                }

                                LinearProgressIndicator(
                                    progress = progress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = progressColor,
                                    trackColor = Color(0xFFE0E0E0)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data model for orders
data class OrderData(
    val orderId: String,
    val items: List<Crop>,
    val totalAmount: Double,
    val status: String,
    val timestamp: Long
)
