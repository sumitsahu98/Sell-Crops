package com.example.authapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EmailVerificationScreen(
    auth: FirebaseAuth,
    showMessage: (String) -> Unit,
    onVerified: () -> Unit,
    email: String
) {
    var isLoading by remember { mutableStateOf(false) }
    var verificationSent by remember { mutableStateOf(false) }

    val user = auth.currentUser

    // Send verification email when screen is first shown
    LaunchedEffect(Unit) {
        if (user != null && !user.isEmailVerified) {
            user.sendEmailVerification()
                .addOnSuccessListener {
                    verificationSent = true
                    showMessage("Verification email sent to $email 📧")
                }
                .addOnFailureListener { e ->
                    showMessage("Failed to send verification email: ${e.message}")
                }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Verify your email",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We sent a verification link to $email. Please check your inbox and click the link.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Check verification button
            Button(
                onClick = {
                    isLoading = true
                    user?.reload()?.addOnCompleteListener {
                        isLoading = false
                        if (user.isEmailVerified) {
                            showMessage("Email verified successfully! 🎉")
                            onVerified()
                        } else {
                            showMessage("Email not verified yet. Please check your inbox.")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("I have verified my email")
            }
        }
    }
}
