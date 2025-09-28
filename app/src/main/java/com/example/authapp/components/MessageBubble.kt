package com.example.authapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.authapp.Screens.Message

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
                    if (msg.isRead) {
                        Row {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Read",
                                tint = Color.Blue,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Read",
                                tint = Color.Blue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Row {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Sent",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                Icons.Default.Done,
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
