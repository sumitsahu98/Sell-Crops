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
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.authapp.components.DatePickerField
import com.example.authapp.ui.components.DefaultTopBar
import com.example.authapp.utils.CloudinaryUploader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenModern(
    auth: FirebaseAuth,
    showMessage: (String) -> Unit,
    onNavigateToEmailVerification: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userId = auth.currentUser?.uid
    val email = auth.currentUser?.email ?: ""
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    // Profile fields
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    var verified by remember { mutableStateOf(false) } // ✅ Verified status

    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMoreInfo by remember { mutableStateOf(false) }

    // Image picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) selectedImageUri = uri }

    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other")

    // Save profile with Cloudinary upload
    fun saveProfile(scope: CoroutineScope) {
        val uid = auth.currentUser?.uid ?: return
        isLoading = true

        scope.launch {
            try {
                var finalImageUrl = profileImageUrl
                if (selectedImageUri != null) {
                    val url = CloudinaryUploader.uploadImage(context, selectedImageUri!!)
                    if (url != null) finalImageUrl = url else showMessage("Failed to upload image")
                }

                val userData = hashMapOf(
                    "fullName" to fullName,
                    "phone" to phone,
                    "address" to address,
                    "city" to city,
                    "state" to state,
                    "country" to country,
                    "gender" to gender,
                    "dob" to dob,
                    "bio" to bio,
                    "website" to website,
                    "profileImageUrl" to finalImageUrl,
                    "verified" to verified // Save verified status
                )

                db.collection("users").document(uid).set(userData).await()
                isLoading = false
                showMessage("Profile updated successfully")
                onBack()
            } catch (e: Exception) {
                isLoading = false
                showMessage("Failed to update profile: ${e.message}")
            }
        }
    }

    // Load profile data
    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                if (snapshot.exists()) {
                    fullName = snapshot.getString("fullName") ?: ""
                    phone = snapshot.getString("phone") ?: ""
                    address = snapshot.getString("address") ?: ""
                    city = snapshot.getString("city") ?: ""
                    state = snapshot.getString("state") ?: ""
                    country = snapshot.getString("country") ?: ""
                    gender = snapshot.getString("gender") ?: ""
                    dob = snapshot.getString("dob") ?: ""
                    bio = snapshot.getString("bio") ?: ""
                    website = snapshot.getString("website") ?: ""
                    profileImageUrl = snapshot.getString("profileImageUrl") ?: ""
                    verified = snapshot.getBoolean("verified") ?: false
                }
            } catch (e: Exception) {
                showMessage("Failed to load profile: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "Edit Profile",
                onBackClick = onBack,
                actions = {
                    TextButton(
                        onClick = { saveProfile(scope) },
                        enabled = !isLoading
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    when {
                        selectedImageUri != null -> AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Gray, shape = CircleShape)
                        )
                        profileImageUrl.isNotEmpty() -> AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Gray, shape = CircleShape)
                        )
                        else -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Gray, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Add\nPhoto", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Button to choose or replace image
                TextButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Choose Photo")
                }

                // Show Remove button if image selected
                if (selectedImageUri != null) {
                    OutlinedButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Remove Photo", color = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Name & Verified Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Text(email, fontSize = 12.sp)
                    if (verified) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Verified",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showMessage("Verification email sent! 📧")
                                        onNavigateToEmailVerification(auth.currentUser?.email ?: "")
                                    } else {
                                        showMessage("Failed to send verification: ${task.exception?.message}")
                                    }
                                }
                            },
                            modifier = Modifier.height(33.dp).padding(0.dp)
                        ) {
                            Text("Verify Now", fontSize = 12.sp)
                        }


                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Account Info Fields
                Text("Account Info", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
                Text("Address Info", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(state, { state = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { showMoreInfo = !showMoreInfo }) {
                    Text(if (showMoreInfo) "Hide More Info" else "Show More Info")
                }

                if (showMoreInfo) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Gender Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedGender,
                        onExpandedChange = { expandedGender = !expandedGender },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            label = { Text("Gender") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGender,
                            onDismissRequest = { expandedGender = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        expandedGender = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Date of Birth
                    DatePickerField(
                        label = "Date of Birth",
                        selectedDate = dob,
                        onDateSelected = { dob = it },
                        pastDatesOnly = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(bio, { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(website, { website = it }, label = { Text("Website") }, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Account", color = Color.White)
                }
            }

            // Delete Account Confirmation
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Account") },
                    text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                if (userId != null) {
                                    isLoading = true
                                    db.collection("users").document(userId).delete()
                                    auth.currentUser?.delete()?.addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            showMessage("Account deleted successfully.")
                                            onBack()
                                        } else {
                                            showMessage("Failed to delete account: ${task.exception?.message}")
                                        }
                                    }
                                }
                            }
                        ) { Text("Delete", color = Color.Red) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    }
                )
            }

            // Loading overlay
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
