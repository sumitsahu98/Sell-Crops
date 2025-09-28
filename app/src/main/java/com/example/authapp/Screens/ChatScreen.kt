package com.example.authapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.authapp.navbars.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Message(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, sellerId: String?) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentUserId = currentUser?.uid ?: return

    val conversationId =
        if (sellerId != null && currentUserId < sellerId) {
            "${currentUserId}_${sellerId}"
        } else {
            "${sellerId}_${currentUserId}"
        }

    var messages by remember { mutableStateOf(listOf<Message>()) }
    var inputText by remember { mutableStateOf("") }

    var userName by remember { mutableStateOf("User") }
    var profileImage by remember { mutableStateOf<String?>(null) }

    // 🔹 Fetch other user info
    LaunchedEffect(sellerId) {
        if (sellerId != null) {
            db.collection("users").document(sellerId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        userName = snapshot.getString("name") ?: "User"
                        profileImage = snapshot.getString("profileImage")
                    }
                }
        }
    }

    // 🔹 Load messages in real time
    LaunchedEffect(conversationId) {
        db.collection("chats")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val msgList = snapshot.toObjects(Message::class.java)
                    messages = msgList

                    // Mark unread messages as read
                    snapshot.documents.forEach { doc ->
                        val msg = doc.toObject(Message::class.java)
                        if (msg != null &&
                            msg.senderId != currentUserId &&
                            msg.isRead.not()
                        ) {
                            db.collection("chats")
                                .document(conversationId)
                                .collection("messages")
                                .document(doc.id)
                                .update("isRead", true)
                        }
                    }
                }
            }
    }
// 🔹 Fetch other user info
    LaunchedEffect(conversationId) {
        val otherUserId = if (sellerId == currentUserId) null else sellerId

        if (otherUserId != null) {
            db.collection("users").document(otherUserId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        userName = snapshot.getString("name") ?: "User"
                        profileImage = snapshot.getString("profileImage")
                    }
                }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (profileImage != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileImage),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.firstOrNull()?.toString() ?: "?",
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(userName, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A1B9A), // Purple
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )

        },
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "chat")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages list
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(messages) { msg ->
                    val isMine = msg.senderId == currentUserId
                    MessageBubble(msg = msg, isMine = isMine)
                }
            }

            // Input box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val text = inputText.trim()
                    if (text.isNotEmpty()) {
                        val newMsg = Message(
                            senderId = currentUserId,
                            message = text,
                            timestamp = System.currentTimeMillis(),
                            isRead = false
                        )
                        db.collection("chats")
                            .document(conversationId)
                            .collection("messages")
                            .add(newMsg)

                        inputText = ""
                    }
                }) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: Message, isMine: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = msg.message,
                    color = if (isMine) Color.White else Color.Black
                )

                if (isMine) {
                    Spacer(modifier = Modifier.width(6.dp))
                    // ✅ Show single or double ticks
                    if (msg.isRead) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Read",
                                tint = Color.Blue,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Read",
                                tint = Color.Blue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Row {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Sent",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Delivered",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
