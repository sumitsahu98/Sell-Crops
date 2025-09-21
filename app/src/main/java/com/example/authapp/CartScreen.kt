package com.example.authapp

//import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {

    Scaffold(
        topBar = {
            CartTopBar(
                navController = navController,
                cartCount = cartViewModel.cartItems.size // ✅ Pass Int, not list
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (cartViewModel.cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🛒 Your cart is empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartViewModel.cartItems) { crop ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(crop.price, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                                    Text("Qty: ${crop.quantity}", fontSize = 12.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { cartViewModel.removeFromCart(crop) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }

                // Subtotal & Checkout
                val total = cartViewModel.cartItems.sumOf {
                    it.price.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Subtotal: ₹$total",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = { /* Navigate to checkout */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Proceed to Checkout", color = Color.White)
                    }
                }
            }
        }
    }
}

// 🔹 CartScreen Top Bar (self-contained)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(navController: NavController, cartCount: Int) {
    TopAppBar(
        title = { Text("Your Cart", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (cartCount > 0) Badge { Text("$cartCount") }
                }
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Cart"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
