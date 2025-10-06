package com.example.authapp.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.authapp.components.CropCard
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.navbars.TopNavBar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import similarity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, cartViewModel: CartViewModel) {

    var searchText by remember { mutableStateOf("") }
    var debouncedSearchText by remember { mutableStateOf("") }
    var crops by remember { mutableStateOf(listOf<Crop>()) }

    // Debounce effect
    LaunchedEffect(searchText) {
        delay(500)
        debouncedSearchText = searchText
    }

    // Fetch crops from Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("crops")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    crops = snapshot.documents.map { doc ->
                        Crop(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getString("price") ?: "",
                            quantity = doc.getString("quantity") ?: "",
                            category = doc.getString("category") ?: "",
                            description = doc.getString("description") ?: "",
                            deliveryDate = doc.getString("deliveryDate") ?: "",
                            location = doc.getString("location") ?: "",
                            sellerId = doc.getString("sellerId") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            imageUrl = doc.getString("imageUrl") ?: ""
                        )
                    }
                }
            }
    }

    val filteredCrops = if (debouncedSearchText.isBlank()) {
        crops
    } else {
        val query = debouncedSearchText.trim().lowercase()
        val threshold = 0.6
        crops.filter { crop ->
            val locationWords = crop.location.trim().split(" ").map { it.lowercase() }
            crop.name.lowercase().contains(query) ||
                    crop.category.lowercase().contains(query) ||
                    locationWords.any { it.contains(query) } ||
                    crop.name.similarity(query) >= threshold ||
                    crop.category.similarity(query) >= threshold ||
                    locationWords.any { it.similarity(query) >= threshold } ||
                    crop.location.lowercase().contains(query) ||
                    crop.location.similarity(query) >= threshold
        }
    }

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
                Icon(Icons.Default.Add, contentDescription = "Add Crop", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
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

                // Categories
                val categories = listOf("Onion", "Tomato", "Wheat", "Rice", "Fruits", "Vegetables")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categories) { category ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color(0xFFDCE775), CircleShape)
                                    .clickable { searchText = category },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(category.take(1), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(category, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Banner
                val bannerColors = listOf(
                    Brush.linearGradient(listOf(Color(0xFF8D3084), Color(0xFFD08282), Color(0xFFD23069))),
                    Brush.linearGradient(listOf(Color(0xFF64B5F6), Color(0xFF2196F3), Color(0xFF0D47A1))),
                    Brush.linearGradient(listOf(Color(0xFFFFB74D), Color(0xFFFF9800), Color(0xFFF57C00))),
                    Brush.linearGradient(listOf(Color(0xFFE57373), Color(0xFFF44336), Color(0xFFB71C1C))),
                    Brush.linearGradient(listOf(Color(0xFF9575CD), Color(0xFF673AB7), Color(0xFF311B92)))
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

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Crops grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 2000.dp) // optional max height to avoid infinite height
                ) {
                    items(filteredCrops) { crop ->
                        CropCard(crop = crop, cartViewModel = cartViewModel, navController = navController)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // padding for FAB and bottom bar
        }
    }
}
