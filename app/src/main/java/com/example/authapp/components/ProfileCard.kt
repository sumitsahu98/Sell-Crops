package com.example.authapp.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileCard(
    fullName: String,
    email: String,
    address: String,
    profileImageUrl: String,
    onEditProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Image
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray, CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFBBDEFB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 30.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(email, fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Address: $address", fontSize = 14.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onEditProfileClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("View & Edit Profile", color = Color.White)
            }
        }
    }
}
