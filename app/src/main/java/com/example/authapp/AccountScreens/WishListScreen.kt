package com.example.authapp.AccountScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar
import com.example.authapp.models.Crop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun WishlistScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var wishlist by remember { mutableStateOf(listOf<Crop>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("wishlists")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            wishlist = snapshot.documents.map { doc ->
                Crop(
                    id = doc.getString("id") ?: "",
                    name = doc.getString("name") ?: "",
                    price = doc.getString("price") ?: "",
                    cartQuantity = doc.getString("quantity") ?: "1",
                    sellerId = doc.getString("sellerId") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            wishlist = emptyList()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = { DefaultTopBar("Wishlist") { navController.navigateUp() } },
        bottomBar = { BottomNavBar(navController, currentRoute = "wishlist") }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                wishlist.isEmpty() -> Text("No items in wishlist", modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(wishlist) { crop ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(crop.name, fontSize = 16.sp)
                                Text("₹${crop.price}", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
