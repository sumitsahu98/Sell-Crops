package com.example.authapp

import LocationPermissionButton
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.authapp.utils.getDetailedLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellCropScreen(navController: NavController) {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    var isLoading by remember { mutableStateOf(false) }

    // Form state
    var cropName by remember { mutableStateOf("") }
    var cropPrice by remember { mutableStateOf("") }
    var cropQuantity by remember { mutableStateOf("") }
    var cropLocation by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }
    var cropDescription by remember { mutableStateOf("") }

    // Dropdown for category
    // Map of categories and example text
    val cropCategories = mapOf(
        "Vegetable" to "e.g., Onion, Tomato, Potato, Spinach",
        "Fruit" to "e.g., Mango, Banana, Apple, Orange",
        "Grain" to "e.g., Rice, Wheat, Corn, Barley",
        "Pulses" to "e.g., Lentils, Chickpeas, Beans, Peas",
        "Other" to ""
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sell Your Crop", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Enter Crop Details", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            // Crop Name
            OutlinedTextField(cropName, { cropName = it }, label = { Text("Crop Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    cropCategories.forEach { (category, example) ->
                        DropdownMenuItem(
                            text = { Text("$category – $example") },
                            onClick = {
                                selectedCategory = category   // ✅ only save the key in database
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Price
            OutlinedTextField(
                value = cropPrice,
                onValueChange = { input -> if (input.all { it.isDigit() }) cropPrice = input },
                label = { Text("Price per kg (₹)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Quantity
            OutlinedTextField(
                value = cropQuantity,
                onValueChange = { input -> if (input.all { it.isDigit() }) cropQuantity = input },
                label = { Text("Quantity (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Location
            OutlinedTextField(cropLocation, { cropLocation = it }, label = { Text("Location / Area") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Button to get detailed location
            LocationPermissionButton {
                getDetailedLocation(context) { detailedAddress ->
                    cropLocation = detailedAddress
                }
            }

            // Delivery Date
            OutlinedTextField(deliveryDate, { deliveryDate = it }, label = { Text("Expected Delivery Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Description
            OutlinedTextField(cropDescription, { cropDescription = it }, label = { Text("Description (Optional)") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 4)

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    val finalCategory = if (selectedCategory == "Other") customCategory else selectedCategory

                    if (userId == null) {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (cropName.isBlank() || cropPrice.isBlank() || cropQuantity.isBlank()) {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    val cropData = hashMapOf(
                        "cropName" to cropName,
                        "cropCategory" to finalCategory,
                        "cropPrice" to cropPrice,
                        "cropQuantity" to cropQuantity,
                        "cropLocation" to cropLocation,
                        "deliveryDate" to deliveryDate,
                        "cropDescription" to cropDescription,
                        "sellerId" to userId,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("crops")
                        .add(cropData)
                        .addOnSuccessListener {
                            isLoading = false
                            Toast.makeText(context, "Crop listed successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            Toast.makeText(context, "Failed to list crop: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Listing..." else "List Crop", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
