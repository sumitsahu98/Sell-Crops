package com.example.authapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellCropScreen(navController: NavController) {

    // State variables for input fields
    var cropName by remember { mutableStateOf("") }
    var cropPrice by remember { mutableStateOf("") }
    var cropQuantity by remember { mutableStateOf("") }
    var cropCategory by remember { mutableStateOf("") }
    var cropLocation by remember { mutableStateOf("") }
    var cropDescription by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sell Your Crop", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50) // Green for agriculture theme
                )
            )
        }
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter Crop Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )

            // Crop Name
            OutlinedTextField(
                value = cropName,
                onValueChange = { cropName = it },
                label = { Text("Crop Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Crop Category
            OutlinedTextField(
                value = cropCategory,
                onValueChange = { cropCategory = it },
                label = { Text("Category (e.g. Vegetable, Grain, Fruit)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Crop Price
            OutlinedTextField(
                value = cropPrice,
                onValueChange = { cropPrice = it },
                label = { Text("Price per kg (₹)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Quantity
            OutlinedTextField(
                value = cropQuantity,
                onValueChange = { cropQuantity = it },
                label = { Text("Quantity (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Location
            OutlinedTextField(
                value = cropLocation,
                onValueChange = { cropLocation = it },
                label = { Text("Location / City") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Delivery Date
            OutlinedTextField(
                value = deliveryDate,
                onValueChange = { deliveryDate = it },
                label = { Text("Expected Delivery Date") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description
            OutlinedTextField(
                value = cropDescription,
                onValueChange = { cropDescription = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    // Save crop logic here
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50), // same as topbar
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("List Crop", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
