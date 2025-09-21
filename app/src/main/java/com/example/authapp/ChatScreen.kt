@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.authapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.ui.components.DefaultTopBar

data class ChatMessage(val sender: String, val message: String, val time: String)

@Composable
fun ChatScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("All", "Buying", "Selling")

    // Sample chat messages state
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("buyer", "Hi, is this still available?", "10:15 AM"),
                ChatMessage("seller", "Yes, I have 50kg left.", "10:16 AM")
            )
        )
    }

    var typedMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "Chats",
                onBackClick = { navController.navigateUp() }
            )
        },bottomBar = { BottomNavBar(navController, currentRoute = "chat") },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filteredMessages = when (selectedTab) {
                    1 -> messages.filter { it.sender == "buyer" }
                    2 -> messages.filter { it.sender == "seller" }
                    else -> messages
                }
                items(filteredMessages) { msg ->
                    val isSender = msg.sender == "seller"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = if (isSender) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .widthIn(max = 250.dp)
                        ) {
                            Text(
                                text = msg.message,
                                color = if (isSender) Color.White else Color.Black,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.time,
                                fontSize = 10.sp,
                                color = if (isSender) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            // Message input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = typedMessage,
                    onValueChange = { typedMessage = it },
                    placeholder = { Text("Type a message...", fontSize = 14.sp, color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
//                    colors = TextFieldDefaults.textFieldColors(
//                        containerColor = Color(0xFFF1F1F1),
//                        textColor = Color.Black,
//                        placeholderColor = Color.Gray,
//                        cursorColor = MaterialTheme.colorScheme.primary
//                    ),
                    shape = RoundedCornerShape(30.dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { /* send action */ },
                            enabled = typedMessage.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (typedMessage.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (typedMessage.isNotBlank()) {
                            messages = messages + ChatMessage(
                                sender = "buyer",
                                message = typedMessage,
                                time = "Now"
                            )
                            typedMessage = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Send", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
