package com.example.authapp.AccountScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar

@Composable
fun HelpSupportScreen(navController: NavController) {
    Scaffold(
        topBar = { DefaultTopBar("Help & Support") { navController.navigateUp() } },
        bottomBar = { BottomNavBar(navController, currentRoute = "help") }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text("FAQs", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Q: How do I place an order?\nA: Go to Home screen, select a crop, and proceed to checkout.", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Q: How can I track my order?\nA: Go to My Orders screen and click 'View'.", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Contact Support", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: support@example.com", fontSize = 14.sp)
            Text("Phone: +91 1234567890", fontSize = 14.sp)
        }
    }
}
