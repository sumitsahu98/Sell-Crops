package com.example.authapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.utils.TranslatorHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    // 👤 Simulate current user ID (you can replace with FirebaseAuth.getInstance().uid)
    private val currentUserId = "user1"

    // 📨 Send a message (translated before saving)
    fun sendMessage(text: String) {
        viewModelScope.launch {
            val message = ChatMessage(
                id = System.currentTimeMillis().toString(),
                senderId = currentUserId,
                receiverId = "user2", // replace dynamically for real chat
                text = text
            )

            // Add locally first
            _messages.value = _messages.value + message

            // 🔧 Here you could also upload to Firestore or Realtime Database
            // e.g., Firebase.firestore.collection("messages").add(message)
        }
    }

    // 📥 Simulate receiving messages from another user
    fun receiveMessage(text: String, senderId: String = "user2") {
        viewModelScope.launch {
            val message = ChatMessage(
                id = System.currentTimeMillis().toString(),
                senderId = senderId,
                receiverId = currentUserId,
                text = text
            )
            _messages.value = _messages.value + message
        }
    }

    // 🌐 Translate all old messages (optional feature)
    fun translateAllMessages(targetLang: String) {
        viewModelScope.launch {
            val translatedList = _messages.value.map { msg ->
                val translatedText = withContext(Dispatchers.IO) {
                    TranslatorHelper.autoTranslateText(msg.text, targetLang)
                }
                msg.copy(text = translatedText)
            }
            _messages.value = translatedList
        }
    }
}
