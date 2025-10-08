package com.example.authapp.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.authapp.models.Crop
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.models.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerCropsScreen(
    auth: FirebaseAuth,
    navController: NavController,
    onEditCrop: (Crop) -> Unit // Callback to navigate to Edit Crop screen
) {
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()
    val cartViewModel: CartViewModel = viewModel()

    var crops by remember { mutableStateOf(listOf<Crop>()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cropToDelete by remember { mutableStateOf<Crop?>(null) }

    // Fetch crops added by this seller
    LaunchedEffect(userId) {
        scope.launch {
            db.collection("crops")
                .whereEqualTo("sellerId", userId)
                .addSnapshotListener { snapshot, _ ->
                    crops = snapshot?.documents?.map { doc ->
                        // Safe function to get a number or string as string
                        fun getStringValue(field: String): String {
                            return when (val value = doc.get(field)) {
                                is Number -> value.toString()
                                is String -> value
                                else -> "0"
                            }
                        }

                        Crop(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = getStringValue("price"),          // ✅ safe for numbers or strings
                            quantity = getStringValue("quantity"),    // ✅ safe for numbers or strings
                            category = doc.getString("category") ?: "",
                            description = doc.getString("description") ?: "",
                            deliveryDate = doc.getString("deliveryDate") ?: "",
                            location = doc.getString("location") ?: "",
                            sellerId = doc.getString("sellerId") ?: "",
                            imageUrl = doc.getString("imageUrl")
                        )
                    } ?: emptyList()
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Crops") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "my_crops")
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                crops.isEmpty() -> {
                    Text(
                        "You have not added any crops yet.",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(crops) { crop ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(Color(0xFFF9F9F9))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Crop Image
                                        Box(
                                            modifier = Modifier
                                                .size(90.dp)
                                                .background(Color.LightGray, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!crop.imageUrl.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = crop.imageUrl,
                                                    contentDescription = crop.name,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Text("No Image", color = Color.Gray, fontSize = 12.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        // Crop details
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(crop.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Price: ₹${crop.price}/kg", fontSize = 14.sp, color = Color(0xFF388E3C))
                                            Text("Quantity: ${crop.quantity} kg", fontSize = 14.sp)
                                            Text("Category: ${crop.category}", fontSize = 14.sp)
                                            Text(
                                                "Status: ${if (crop.quantity.toIntOrNull() ?: 0 > 0) "Available" else "Sold"}",
                                                fontSize = 14.sp,
                                                color = if (crop.quantity.toIntOrNull() ?: 0 > 0) Color(0xFF388E3C) else Color.Red
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Edit & Delete Buttons
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        IconButton(onClick = { onEditCrop(crop) }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF4CAF50))
                                        }
                                        IconButton(onClick = {
                                            cropToDelete = crop
                                            showDeleteDialog = true
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Delete confirmation dialog
            if (showDeleteDialog && cropToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Crop") },
                    text = { Text("Are you sure you want to delete ${cropToDelete!!.name}? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(onClick = {
                            db.collection("crops").document(cropToDelete!!.id).delete()
                            showDeleteDialog = false
                        }) {
                            Text("Delete", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
