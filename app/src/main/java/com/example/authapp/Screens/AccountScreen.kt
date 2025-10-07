package com.example.authapp.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.authapp.components.ProfileCard
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun AccountScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    var fullName by remember { mutableStateOf("Loading...") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(auth.currentUser?.email ?: "") }
    var profileImageUrl by remember { mutableStateOf("") }

    // Fetch user details
    LaunchedEffect(isLoggedIn) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                if (snapshot.exists()) {
                    fullName = snapshot.getString("fullName") ?: "Unknown"
                    address = snapshot.getString("address") ?: ""
                    profileImageUrl = snapshot.getString("profileImageUrl") ?: ""
                    email = auth.currentUser?.email ?: ""
                }
            } catch (e: Exception) {
                fullName = "Error: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "My Account",
                onBackClick = { navController.navigateUp() }
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = "account") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoggedIn) {
                // Profile Section
                ProfileCard(
                    fullName = fullName,
                    email = email,
                    address = address,
                    profileImageUrl = profileImageUrl,
                    onEditProfileClick = { navController.navigate("edit_profile") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Options List
                val options = listOf(
                    AccountOptionData(
                        title = "Buy Packages & My Orders",
                        onClick = { navController.navigate("orders") }
                    ),
                    AccountOptionData(
                        title = "Wishlist",
                        onClick = {
                            Toast.makeText(context, "Wishlist clicked!", Toast.LENGTH_SHORT).show()
                            navController.navigate("wishlist")
                        }
                    )
                    ,
                    AccountOptionData(
                        title = "Become an Elite Buyer",
                        isNew = true,
                        onClick = { navController.navigate("elite") }
                    ),
                    AccountOptionData(
                        title = "Settings",
                        onClick = { navController.navigate("settings") }
                    ),
                    AccountOptionData(
                        title = "Help & Support",
                        onClick = { navController.navigate("help") }
                    )
                )


                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { option.onClick() }
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(option.title, fontSize = 16.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            if (option.isNew) {
                                Text(
                                    "New",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color.Red, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(">", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = {
                        auth.signOut()
                        isLoggedIn = false
                        navController.navigate("login") {
                            popUpTo("account") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout", color = Color.White)
                }
            } else {
                // If NOT logged in
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("You are not logged in", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Login")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { navController.navigate("signup") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Account")
                    }
                }
            }
        }
    }
}

// ==========================
// Account Option Data Class
// ==========================
data class AccountOptionData(
    val title: String,
    val isNew: Boolean = false,
    val onClick: () -> Unit
)
