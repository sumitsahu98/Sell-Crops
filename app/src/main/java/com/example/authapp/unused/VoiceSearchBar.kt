package com.example.authapp.Screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSearchBar() {
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Voice input launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenText.isNullOrEmpty()) {
                searchText = spokenText[0]
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF1F1F1), shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search input
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                textColor = Color.Black,
                placeholderColor = Color.Gray
            )
        )

        // Mic button
        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
                voiceLauncher.launch(intent)
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF4CAF50), shape = CircleShape)
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.White)
        }

        // Clear text button
        if (searchText.isNotEmpty()) {
            IconButton(
                onClick = { searchText = "" },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF44336), shape = CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
            }
        }
    }
}
