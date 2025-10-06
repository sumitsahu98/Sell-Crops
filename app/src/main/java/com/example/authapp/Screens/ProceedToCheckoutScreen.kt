package com.example.authapp.Screens

import LocationPermissionButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.example.authapp.utils.getDetailedLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val selectedItems: List<Crop> = cartViewModel.selectedForCheckout

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isFetchingLocation by remember { mutableStateOf(false) }

    val subtotal = selectedItems.sumOf { crop ->
        val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
        val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
        priceFor10Kg * cartQty / 10
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        // Make the whole content scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Cart Items
            Text("Your Items", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            selectedItems.forEach { crop ->
                CartItemRow(crop)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shipping Details
            Text("Shipping Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Address input + current location button
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                LocationPermissionButton {
                    getDetailedLocation(context) { detailedAddress ->
                        address = detailedAddress
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtotal & Pay Now button
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Subtotal: ₹${subtotal.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        // TODO: handle order submission / payment
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = name.isNotBlank() && address.isNotBlank() && phone.isNotBlank()
                ) {
                    Text("Pay Now", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(crop: Crop) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Crop Image
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .weight(0.4f)
                    .aspectRatio(1.5f),
                contentAlignment = Alignment.Center
            ) {
                if (!crop.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = crop.imageUrl,
                        contentDescription = crop.name,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("No Image", fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Crop details
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(0.6f)
            ) {
                Text(crop.name, fontWeight = FontWeight.Bold)
                val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
                val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
                Text(
                    "₹${(priceFor10Kg * cartQty / 10).toInt()} (${cartQty}kg)",
                    color = Color.Gray
                )
            }
        }
    }
}
