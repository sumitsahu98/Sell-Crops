//package com.example.authapp//package com.example.authapp
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
////import com.example.sumitsapp.AppBottomNavBar
//import com.google.firebase.auth.FirebaseAuth
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AcccountScreen(navController: NavController) {
//    val auth = FirebaseAuth.getInstance()
//    val currentUser = auth.currentUser
//    val isLoggedIn = currentUser != null
//    val userName = currentUser?.email ?: "User"
//
//    Scaffold(
////        bottomBar = { AppBottomNavBar(navController, currentRoute = "account") }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//            Text(
//                text = "My Account",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            if (isLoggedIn) {
//                // 🔹 Profile Section
//                Card(
//                    shape = RoundedCornerShape(12.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .size(60.dp)
//                                .background(Color(0xFFBBDEFB), CircleShape),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("👤", fontSize = 30.sp)
//                        }
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Column {
//                            Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                            Button(
//                                onClick = { /* Navigate to edit profile */ },
//                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
//                                modifier = Modifier.padding(top = 8.dp)
//                            ) {
//                                Text("View & Edit Profile", color = Color.White)
//                            }
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // 🔹 Options List
//                val options = listOf(
//                    AccountOptionData("Buy Packages & My Orders") { navController.navigate("orders") },
//                    AccountOptionData("Wishlist") { /* Navigate */ },
//                    AccountOptionData("Become an Elite Buyer", isNew = true) { /* Navigate */ },
//                    AccountOptionData("Settings") { /* Navigate */ },
//                    AccountOptionData("Help & Support") { /* Navigate */ }
//                )
//
//                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                    options.forEach { option ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { option.onClick() }
//                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
//                                .padding(16.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(option.title, fontSize = 16.sp)
//                            Spacer(modifier = Modifier.weight(1f))
//                            if (option.isNew) {
//                                Text(
//                                    "New",
//                                    fontSize = 12.sp,
//                                    color = Color.White,
//                                    modifier = Modifier
//                                        .background(Color.Red, RoundedCornerShape(4.dp))
//                                        .padding(horizontal = 6.dp, vertical = 2.dp)
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                            }
//                            Text(">", fontSize = 16.sp, color = Color.Gray)
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // 🔹 Logout Button
//                Button(
//                    onClick = {
//                        auth.signOut()
//                        navController.navigate("login") {
//                            popUpTo("account") { inclusive = true }
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Logout", color = Color.White)
//                }
//            } else {
//                // 🔹 If NOT logged in
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("You are not logged in", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Button(
//                        onClick = { navController.navigate("login") },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Login")
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    OutlinedButton(
//                        onClick = { navController.navigate("signup") },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Create Account")
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ==========================
//// Account Option Data Class
//// ==========================
//data class AcccountOptionData(
//    val title: String,
//    val isNew: Boolean = false,
//    val onClick: () -> Unit
//)
