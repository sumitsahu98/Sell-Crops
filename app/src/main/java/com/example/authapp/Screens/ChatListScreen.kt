package com.example.authapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.navbars.BottomNavBar
import com.example.authapp.ui.components.DefaultTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import formatTimestamp

data class ChatUser(
    val userId: String,
    val name: String = "Unknown User",
    val role: String = "Buyer",
    val profileImageUrl: String? = null,
    val lastMessage: String? = null,
    val lastTimestamp: Long? = null,
    val unreadCount: Int = 0
)

@Composable
fun ChatListScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid

    var chatUsers by remember { mutableStateOf(listOf<ChatUser>()) }
    var filter by remember { mutableStateOf("All") }

    // 🔹 Real-time listener for chats
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            db.collection("chats")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot == null) return@addSnapshotListener
                    val tempList = mutableListOf<ChatUser>()

                    snapshot.documents.forEach { chatDoc ->
                        val participants = chatDoc.get("participants") as? Map<String, String> ?: return@forEach
                        if (!participants.containsKey(currentUserId)) return@forEach

                        val otherUserId = participants.keys.first { it != currentUserId }
                        val otherUserRole = participants[otherUserId] ?: "Buyer" // ✅ Correct role

                        // Fetch messages for last message + unread count
                        chatDoc.reference.collection("messages")
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { msgSnap ->
                                if (msgSnap.isEmpty) return@addOnSuccessListener // Skip if no messages

                                val lastDoc = msgSnap.documents.first()
                                val lastMessage = lastDoc.getString("message")
                                val lastTimestamp = lastDoc.getLong("timestamp")

                                val unreadCount = msgSnap.documents.count { msg ->
                                    val senderId = msg.getString("senderId")
                                    val isRead = msg.getBoolean("isRead") ?: false
                                    senderId != currentUserId && !isRead
                                }

                                // Fetch other user info
                                db.collection("users").document(otherUserId)
                                    .get()
                                    .addOnSuccessListener { userDoc ->
                                        tempList.add(
                                            ChatUser(
                                                userId = otherUserId,
                                                name = userDoc.getString("name") ?: "User",
                                                role = otherUserRole, // ✅ Correct role
                                                profileImageUrl = userDoc.getString("profileImageUrl"),
                                                lastMessage = lastMessage,
                                                lastTimestamp = lastTimestamp,
                                                unreadCount = unreadCount
                                            )
                                        )
                                        chatUsers = tempList.sortedByDescending { it.lastTimestamp ?: 0L }
                                    }
                            }
                    }
                }
        }
    }

    Scaffold(
        topBar = { DefaultTopBar(title = "Chats", onBackClick = { navController.popBackStack() }) },
        bottomBar = { BottomNavBar(navController, currentRoute = "chatList") }
    ) { paddingValues ->
        if (currentUserId == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Please login to see your chats", fontSize = 16.sp, color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 🔹 Filter buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterButton("All", filter) { filter = "All" }
                    FilterButton("Seller", filter) { filter = "Seller" }
                    FilterButton("Buyer", filter) { filter = "Buyer" }
                }

                val filteredList = when (filter) {
                    "Seller" -> chatUsers.filter { it.role.equals("Seller", ignoreCase = true) }
                    "Buyer" -> chatUsers.filter { it.role.equals("Buyer", ignoreCase = true) }
                    else -> chatUsers
                }

                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No chats available", fontSize = 16.sp, color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredList) { chatUser ->
                            ChatUserRow(chatUser = chatUser) {
                                navController.navigate("chat/${chatUser.userId}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatUserRow(chatUser: ChatUser, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chatUser.profileImageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(chatUser.profileImageUrl),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chatUser.name.firstOrNull()?.toString() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(chatUser.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (chatUser.lastTimestamp != null) {
                    Text(
                        text = formatTimestamp(chatUser.lastTimestamp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(chatUser.role, fontSize = 12.sp, color = Color.Gray)
            if (!chatUser.lastMessage.isNullOrEmpty()) {
                Text(
                    text = chatUser.lastMessage,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }
        }

        if (chatUser.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chatUser.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterButton(text: String, currentFilter: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = if (currentFilter == text) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        } else {
            ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        }
    ) {
        Text(text)
    }
}
