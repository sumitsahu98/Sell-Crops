package com.example.authapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    title: String = "New Delhi, India",      // Display location or screen title
    cartCount: Int = 0,                       // Badge count
    onCartClick: (() -> Unit)? = null         // Action when cart icon is clicked
) {
    TopAppBar(
        title = {
            Column {
                Text("Current Location", fontSize = 14.sp, color = Color.Gray)
                Text(title, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            IconButton(onClick = { onCartClick?.invoke() }) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge { Text("$cartCount") }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )


    )
}
