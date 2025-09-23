package com.example.authapp.Screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun AccountScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    var fullName by remember { mutableStateOf("Loading...") }
//    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // 🔹 Fetch details when user is logged in
    var email by remember { mutableStateOf(auth.currentUser?.email ?: "") }

    LaunchedEffect(isLoggedIn) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                if (snapshot.exists()) {
                    fullName = snapshot.getString("fullName") ?: "Unknown"
                    address = snapshot.getString("address") ?: ""
                    // Don't fetch email from Firestore; use FirebaseAuth email
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
                // 🔹 Profile Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color(0xFFBBDEFB), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 30.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(email, fontSize = 14.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Address: $address", fontSize = 14.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { navController.navigate("edit_profile") }, // ✅ Navigate to edit profile
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                        ) {
                            Text("View & Edit Profile", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔹 Options List
                val options = listOf(
                    AccountOptionData("Buy Packages & My Orders") { navController.navigate("orders") },
                    AccountOptionData("Wishlist") { /* Navigate */ },
                    AccountOptionData("Become an Elite Buyer", isNew = true) { /* Navigate */ },
                    AccountOptionData("Settings") { /* Navigate */ },
                    AccountOptionData("Help & Support") { /* Navigate */ }
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

                // 🔹 Logout Button
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
                // 🔹 If NOT logged in
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
