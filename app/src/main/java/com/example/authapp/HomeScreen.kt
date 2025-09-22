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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.components.AutoSlidingBanner
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
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 Categories
            val categories = listOf("Onion", "Tomato", "Wheat", "Rice", "Fruits", "Vegetables")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
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

            Spacer(modifier = Modifier.height(1.dp))

            // 🔹 Banner
            val bannerColors = listOf(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF8D3084), Color(0xFFD08282), Color(0xFFD23069))
                ),
                Brush.linearGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF2196F3), Color(0xFF0D47A1))
                ),
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800), Color(0xFFF57C00))
                ),
                Brush.linearGradient(
                    colors = listOf(Color(0xFFE57373), Color(0xFFF44336), Color(0xFFB71C1C))
                ),
                Brush.linearGradient(
                    colors = listOf(Color(0xFF9575CD), Color(0xFF673AB7), Color(0xFF311B92))
                )
            )

            val bannerMessages = listOf(
                "🌾 Sell crops directly to buyers & get better prices!",
                "🚜 Join thousands of farmers and increase your profit!",
                "🥬 Fresh vegetables and grains available near you!",
                "📦 List your crops easily and track your sales!",
                "💰 Maximize your earnings with direct buyers!"
            )

            AutoSlidingBanner(
                messages = bannerMessages,
                colors = bannerColors,
                slideDurationMs = 3000,
                animationDurationMs = 1200
            )

            Spacer(modifier = Modifier.height(1.dp))

            // 🔹 Fresh Listings
            Text(
                "Fresh Listings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

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
