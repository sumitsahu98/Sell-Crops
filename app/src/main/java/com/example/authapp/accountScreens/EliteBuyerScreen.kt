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
fun EliteBuyerScreen(navController: NavController) {
    Scaffold(
        topBar = { DefaultTopBar("Elite Buyer") { navController.navigateUp() } },
        bottomBar = { BottomNavBar(navController, currentRoute = "elite") }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Become an Elite Buyer",
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Elite buyers get exclusive discounts, early access to new crops, and priority support. " +
                        "Join now to unlock these benefits!",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { /* Implement purchase/join logic */ }) {
                Text("Join Elite Buyer Program")
            }
        }
    }
}
