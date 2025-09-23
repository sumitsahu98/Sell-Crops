package com.example.authapp.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignupScreen(
    auth: FirebaseAuth,
    showMessage: (String) -> Unit,
    onNavigateToHome: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // ✅ Loading state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()), // ✅ Make scrollable when keyboard appears
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create your account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign up to get started with us 🚀",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Sign Up Button
            Button(
                onClick = {
                    if (fullName.isBlank() || phone.isBlank() || address.isBlank() || email.isBlank() || password.isBlank()) {
                        showMessage("Please fill all fields")
                    } else {
                        isLoading = true // ✅ Show loading
                        auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val signInMethods = task.result?.signInMethods ?: emptyList()
                                    if (signInMethods.isNotEmpty()) {
                                        isLoading = false
                                        showMessage("This email is already registered. Please login.")
                                    } else {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { signupTask ->
                                                isLoading = false // ✅ Hide loading
                                                if (signupTask.isSuccessful) {
                                                    val userId = signupTask.result?.user?.uid
                                                    if (userId != null) {
                                                        val user = hashMapOf(
                                                            "fullName" to fullName,
                                                            "phone" to phone,
                                                            "address" to address,
                                                            "email" to email,
                                                            "profileUrl" to null,
                                                            "city" to null,
                                                            "state" to null,
                                                            "country" to null,
                                                            "gender" to null,
                                                            "dob" to null,
                                                            "bio" to null,
                                                            "website" to null,
                                                            "socialLinks" to mapOf(
                                                                "facebook" to null,
                                                                "instagram" to null,
                                                                "twitter" to null,
                                                                "linkedin" to null
                                                            ),
                                                            "preferences" to mapOf(
                                                                "newsletter" to false,
                                                                "notifications" to true
                                                            )
                                                        )


                                                        FirebaseFirestore.getInstance()
                                                            .collection("users")
                                                            .document(userId)
                                                            .set(user)
                                                            .addOnSuccessListener {
                                                                showMessage("Signup successful! 🎉")
                                                                onNavigateToHome() // ✅ Redirect to login
                                                            }
                                                            .addOnFailureListener { e ->
                                                                showMessage("Failed to save details: ${e.message}")
                                                            }
                                                    } else {
                                                        showMessage("Signup successful, but userId is null")
                                                    }
                                                } else {
                                                    showMessage("Error: ${signupTask.exception?.message}")
                                                }
                                            }
                                    }
                                } else {
                                    isLoading = false
                                    showMessage("Error: ${task.exception?.message}")
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign Up",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row {
                Text("Already have an account? ", color = Color.Gray)
                TextButton(onClick = onNavigateToHome) {
                    Text("Login", color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)), // semi-transparent overlay
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF4A90E2))
            }
        }
    }
}
