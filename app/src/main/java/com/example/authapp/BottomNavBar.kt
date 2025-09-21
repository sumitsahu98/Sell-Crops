package com.example.authapp

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = { Text("🏠") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "chat",
            onClick = { navController.navigate("chat") },
            icon = { Text("💬") },
            label = { Text("Chats") }
        )
        NavigationBarItem(
            selected = currentRoute == "orders",
            onClick = { navController.navigate("orders") },
            icon = { Text("📦") },
            label = { Text("Orders") }

        )
        NavigationBarItem(
            selected = currentRoute == "account",
            onClick = { navController.navigate("account") },
            icon = { Text("👤") },
            label = { Text("Account") }
        )
    }
}
