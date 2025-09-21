package com.example.authapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.text.take

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, cartViewModel: CartViewModel) {

    var searchText by remember { mutableStateOf("") }

    val crops = listOf(
        Crop("Onion", "₹15/kg", "200kg"),
        Crop("Tomato", "₹20/kg", "150kg"),
        Crop("Wheat", "₹25/kg", "500kg"),
        Crop("Rice", "₹30/kg", "300kg"),
        Crop("Potato", "₹12/kg", "400kg")
    )

    val filteredCrops = crops.filter { it.name.contains(searchText, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopNavBar(
                title = "New Delhi, India",
                cartCount = cartViewModel.cartItems.size,
                onCartClick = { navController.navigate("cart") }
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = "home") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create") },
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Crop",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Search Bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                placeholder = { Text("Search crops: Onion, Tomato...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Categories
            val categories = listOf("Onion", "Tomato", "Wheat", "Rice", "Fruits", "Vegetables")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFDCE775), CircleShape)
                                .clickable { /* filter by category */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(category.take(1), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(category, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "🌾 Sell crops directly to buyers & get better prices!",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Fresh Listings
            Text(
                "Fresh Listings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 🔹 Two crops per row
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(filteredCrops) { crop ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .clickable { navController.navigate("details") },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Image
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Img")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(crop.price, color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                            Text("Available: ${crop.quantity}", fontSize = 10.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { cartViewModel.addToCart(crop) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text("Add", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🔹 Crop Data Model
data class Crop(val name: String, val price: String, val quantity: String)
