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
import java.text.SimpleDateFormat
import java.util.*

// Chat user model with extra fields
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

    // 🔹 Fetch conversations with last message + unread count
    LaunchedEffect(Unit) {
        if (currentUserId != null) {
            db.collection("chats")
                .get()
                .addOnSuccessListener { snapshot ->
                    val userList = mutableSetOf<ChatUser>()

                    for (doc in snapshot.documents) {
                        val conversationId = doc.id
                        if (conversationId.contains(currentUserId)) {
                            val parts = conversationId.split("_")
                            val otherUserId =
                                if (parts[0] == currentUserId) parts[1] else parts[0]

                            // fetch last message + unread count
                            db.collection("chats")
                                .document(conversationId)
                                .collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener { msgSnapshot ->
                                    val lastMsgDoc = msgSnapshot.documents.firstOrNull()
                                    val lastMessage = lastMsgDoc?.getString("message")
                                    val lastTimestamp = lastMsgDoc?.getLong("timestamp")

                                    // count unread messages
                                    val unreadCount = msgSnapshot.documents.count { message ->
                                        val senderId = message.getString("senderId")
                                        val isRead = message.getBoolean("isRead") ?: false
                                        senderId != currentUserId && !isRead
                                    }

                                    // fetch user info
                                    db.collection("users").document(otherUserId)
                                        .get()
                                        .addOnSuccessListener { userDoc ->
                                            val name = userDoc.getString("name") ?: "User"
                                            val role = userDoc.getString("role") ?: "Buyer"
                                            val profileImage =
                                                userDoc.getString("profileImageUrl")

                                            userList.add(
                                                ChatUser(
                                                    userId = otherUserId,
                                                    name = name,
                                                    role = role,
                                                    profileImageUrl = profileImage,
                                                    lastMessage = lastMessage,
                                                    lastTimestamp = lastTimestamp,
                                                    unreadCount = unreadCount
                                                )
                                            )
                                            chatUsers = userList.toList()
                                        }
                                }
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "Chats",
                onBackClick = { navController.popBackStack() }
            )
        },
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
        // Profile image or initial
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

        // 🔹 Show unread count badge
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

// 🔹 Timestamp formatter
fun formatTimestamp(timestamp: Long?): String {
    if (timestamp == null) return ""
    val now = System.currentTimeMillis()
    val date = Date(timestamp)

    return when {
        android.text.format.DateUtils.isToday(timestamp) -> {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        }
        android.text.format.DateUtils.isToday(timestamp + android.text.format.DateUtils.DAY_IN_MILLIS) -> {
            "Yesterday"
        }
        else -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
        }
    }
}
