package com.example.authapp.navbars

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit // ✅ added
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.authapp.utils.getDetailedLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    cartCount: Int = 0,
    onCartClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Fetching location...") }
    var showAddressDialog by remember { mutableStateOf(false) } // ✅ new state

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getDetailedLocation(context) { address ->
                locationText = address
            }
        } else {
            locationText = "Location permission denied"
        }
    }

    // Check permission and fetch location
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            getDetailedLocation(context) { address ->
                locationText = address
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // ✅ Manual Address Dialog
    if (showAddressDialog) {
        var manualAddress by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Enter Address") },
            text = {
                OutlinedTextField(
                    value = manualAddress,
                    onValueChange = { manualAddress = it },
                    label = { Text("Your address") },
                    singleLine = false
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (manualAddress.isNotBlank()) {
                        locationText = manualAddress
                        showAddressDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Top App Bar
    TopAppBar(
        title = {
            Column {
                Text(
                    "Current Location",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = locationText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            // ✅ Added edit icon to manually set address
            IconButton(onClick = { showAddressDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Address")
            }

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
