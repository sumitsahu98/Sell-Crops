package com.example.authapp.Screens

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.authapp.models.Crop
import com.example.authapp.utils.CloudinaryUploader
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCropScreen(
    crop: Crop,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(crop.name) }
    var price by remember { mutableStateOf(crop.price) }
    var quantity by remember { mutableStateOf(crop.quantity) }
    var category by remember { mutableStateOf(crop.category) }
    var description by remember { mutableStateOf(crop.description) }
    var deliveryDate by remember { mutableStateOf(crop.deliveryDate) }
    var location by remember { mutableStateOf(crop.location) }
    var imageUrl by remember { mutableStateOf(crop.imageUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) selectedImageUri = uri
    }

    fun saveCrop() {
        isLoading = true
        scope.launch {
            try {
                var finalImageUrl = imageUrl
                if (selectedImageUri != null) {
                    val url = CloudinaryUploader.uploadImage(context, selectedImageUri!!)
                    if (url != null) finalImageUrl = url
                }

                val updatedCrop: Map<String, Any> = hashMapOf(
                    "name" to name,
                    "price" to price,
                    "quantity" to quantity,
                    "category" to category,
                    "description" to description,
                    "deliveryDate" to deliveryDate,
                    "location" to location,
                    "imageUrl" to finalImageUrl
                ) as Map<String, Any> // <-- Cast to Map<String, Any>

                db.collection("crops").document(crop.id).update(updatedCrop).await()
                isLoading = false
                onBack()
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Crop") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize()
                        )
                        imageUrl.isNotEmpty() -> AsyncImage(
                            model = imageUrl,
                            contentDescription = "Crop Image",
                            modifier = Modifier.fillMaxSize()
                        )
                        else -> Icon(Icons.Default.Image, contentDescription = "Add Image", tint = Color.White)
                    }
                }

                TextButton(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Choose Image")
                }

                // Crop Details
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price per kg") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity (kg)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = deliveryDate, onValueChange = { deliveryDate = it }, label = { Text("Delivery Date") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())

                Button(
                    onClick = { saveCrop() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}
