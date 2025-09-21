package com.example.authapp

import DateOfBirthField
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.ui.components.DefaultTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenModern(
    auth: FirebaseAuth,
    showMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    val userId = auth.currentUser?.uid
    val email = auth.currentUser?.email ?: ""
    val db = FirebaseFirestore.getInstance()

    // State variables
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
    var isLoading by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMoreInfo by remember { mutableStateOf(false) }

    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other")

    fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
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
            "website" to website
        )

        isLoading = true
        db.collection("users").document(uid).set(userData)
            .addOnSuccessListener {
                isLoading = false
                showMessage("Profile updated successfully")
                onBack()
            }
            .addOnFailureListener { e ->
                isLoading = false
                showMessage("Failed to update profile: ${e.message}")
            }
    }


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
                }
            } catch (e: Exception) {
                showMessage("Failed to load profile: ${e.message}")
            }
        }
    }

    // Load data from Firestore (same as before)...

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "Edit Profile",
                onBackClick = onBack,
                actions = {
                    TextButton(
                        onClick = { saveProfile() },
                        enabled = !isLoading
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                }
            )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Picture placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(50.dp))
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Add\nPhoto", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Account Info Section
            Text("Account Info", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(email, onValueChange = {}, label = { Text("Email") }, enabled = false, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Text("Address Info", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(state, onValueChange = { state = it }, label = { Text("State") }, modifier = Modifier.weight(1f))
                OutlinedTextField(country, onValueChange = { country = it }, label = { Text("Country") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Collapsible More Info
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
                        modifier = Modifier
                            .menuAnchor() // Important for proper dropdown positioning
                            .fillMaxWidth()
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

                // Date of Birth Picker

                OutlinedTextField(dob, onValueChange = { dob = it }, label = { Text("Date of Birth") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Website
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth()
                )
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

        // Delete Dialog same as previous code...


            // Delete confirmation dialog
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
                                    // Delete Firestore document
                                    db.collection("users").document(userId).delete()
                                    // Delete Firebase Auth account
                                    auth.currentUser?.delete()?.addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            showMessage("Account deleted successfully.")
                                            onBack() // navigate back or to login
                                        } else {
                                            showMessage("Failed to delete account: ${task.exception?.message}")
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Delete", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }






}
