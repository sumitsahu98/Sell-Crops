package com.example.authapp.Screens

import LocationPermissionButton
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.authapp.components.DatePickerField
import com.example.authapp.models.Crop
import com.example.authapp.utils.CloudinaryUploader
import com.example.authapp.utils.getDetailedLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellCropScreen(navController: androidx.navigation.NavController) {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // Form state
    var cropName by remember { mutableStateOf("") }
    var cropPrice by remember { mutableStateOf("") }
    var cropQuantity by remember { mutableStateOf("") }
    var cropLocation by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }
    var cropDescription by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf("") }

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) selectedImageUri = uri
    }

    // Dropdown for category
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

            Text(
                "Enter Crop Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )

            // Image Picker Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    // Show selected image
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Crop Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { imagePickerLauncher.launch("image/*") } // tap to replace
                    )
                } else {
                    // Show button text
                    Text(
                        "Tap to select crop image",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
                    )
                }
            }

            // Show "Remove Image" button if image is selected
            if (selectedImageUri != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove Image", color = Color.Red)
                }
            }

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
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Crop Name
            OutlinedTextField(
                value = cropName,
                onValueChange = { cropName = it },
                label = { Text("Crop Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (selectedCategory == "Other") {
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Enter Custom Category") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
            OutlinedTextField(
                value = cropLocation,
                onValueChange = { cropLocation = it },
                label = { Text("Location / Area") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Button to get detailed location
            LocationPermissionButton {
                getDetailedLocation(context) { detailedAddress ->
                    cropLocation = detailedAddress
                }
            }

            // Delivery Date
            DatePickerField(
                label = "Expected Delivery Date",
                selectedDate = deliveryDate,
                onDateSelected = { deliveryDate = it },
                futureDatesOnly = true
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
                    val finalCategory =
                        if (selectedCategory == "Other") customCategory else selectedCategory

                    if (userId == null) {
                        Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (cropName.isBlank() || cropPrice.isBlank() || cropQuantity.isBlank()) {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    isLoading = true

                    scope.launch {
                        // Upload image to Cloudinary if selected
                        if (selectedImageUri != null) {
                            val url = CloudinaryUploader.uploadImage(context, selectedImageUri!!)
                            uploadedImageUrl = url ?: ""
                        }

                        val crop = Crop(
                            name = cropName,
                            category = finalCategory,
                            price = cropPrice,
                            quantity = cropQuantity,
                            location = cropLocation,
                            deliveryDate = deliveryDate,
                            description = cropDescription,
                            imageUrl = uploadedImageUrl,
                            sellerId = userId
                        )

                        db.collection("crops")
                            .add(crop)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Crop listed successfully", Toast.LENGTH_SHORT)
                                    .show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Failed to list crop: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                Text(
                    if (isLoading) "Listing..." else "List Crop",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
