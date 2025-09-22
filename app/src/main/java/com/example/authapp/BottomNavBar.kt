package com.example.authapp

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        BottomNavItem("home", "Home", Icons.Filled.Home),
        BottomNavItem("chat", "Chats", Icons.Filled.Message),
        BottomNavItem("orders", "Orders", Icons.Filled.ShoppingCart),
        BottomNavItem("account", "Account", Icons.Filled.AccountCircle)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 12.dp // stronger modern shadow
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.3f else 1f,
                label = "iconScale"
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size((24 * scale).dp),
                        tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                },
                label = {
                    if (selected) {
                        Text(
                            text = item.label,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                alwaysShowLabel = false
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
