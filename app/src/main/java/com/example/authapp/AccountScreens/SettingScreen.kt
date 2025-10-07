package com.example.authapp.AccountScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar

@Composable
fun SettingsScreen(navController: NavController) {
    val options = listOf(
        "Account Settings",
        "Notifications",
        "Privacy & Security",
        "Language",
        "About App"
    )

    Scaffold(
        topBar = { DefaultTopBar("Settings") { navController.navigateUp() } },
        bottomBar = { BottomNavBar(navController, currentRoute = "settings") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Navigate to option */ }
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(option, fontSize = 16.sp)
                }
            }
        }
    }
}
