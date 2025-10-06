package com.example.authapp.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.authapp.components.AddToCartButton
import com.example.authapp.models.CartViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val context = LocalContext.current

    val selectedItems = remember { mutableStateMapOf<String, Boolean>() }

    // Initialize selected items to true
    cartViewModel.cartItems.forEach { crop ->
        if (selectedItems[crop.id] == null) selectedItems[crop.id] = true
    }

    Scaffold(
        topBar = {
            CartTopBar(
                navController = navController,
                cartCount = cartViewModel.cartItems.size
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Left Column: Checkbox + Crop Info
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Checkbox on the left
                                        Checkbox(
                                            checked = selectedItems[crop.id] == true,
                                            onCheckedChange = { selectedItems[crop.id] = it }
                                        )

                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = crop.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF212121),
                                                maxLines = 1
                                            )

                                            val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
                                            Text(
                                                text = "₹ ${priceFor10Kg.toInt()}/10kg",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF388E3C)
                                            )

                                            val available = (crop.quantity.toIntOrNull() ?: 0) - (crop.cartQuantity.toIntOrNull() ?: 0)
                                            Text(
                                                text = "$available kg available",
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Right Column: Crop Image
                                    Box(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!crop.imageUrl.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = crop.imageUrl,
                                                contentDescription = crop.name,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text("No Image", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                AddToCartButton(
                                    crop = crop,
                                    cartViewModel = cartViewModel,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Subtotal & Proceed to Checkout
                val total = cartViewModel.cartItems.filter { selectedItems[it.id] == true }
                    .sumOf { crop ->
                        val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
                        val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
                        priceFor10Kg * cartQty / 10
                    }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Subtotal: ₹${total.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = {
                            val selectedCrops = cartViewModel.cartItems.filter { selectedItems[it.id] == true }
                            if (selectedCrops.isEmpty()) {
                                Toast.makeText(context, "Please select at least one item", Toast.LENGTH_SHORT).show()
                            } else {
                                cartViewModel.selectedForCheckout = selectedCrops
                                navController.navigate("checkout")
                            }
                        },
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
            Box(modifier = Modifier.padding(end = 12.dp)) {
                BadgedBox(
                    badge = { if (cartCount > 0) Badge { Text("$cartCount") } }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
