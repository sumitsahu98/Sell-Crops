import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

fun startChatWithSeller(
    buyerId: String,
    sellerId: String,
    onChatReady: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // ✅ Unique chatId (buyer_seller sorted so same id is reused)
    val chatId = listOf(buyerId, sellerId).sorted().joinToString("_")

    val chatRef = db.collection("chats").document(chatId)

    chatRef.get().addOnSuccessListener { doc ->
        if (!doc.exists()) {
            // ✅ Create new chat
            val chatData = mapOf(
                "participants" to listOf(buyerId, sellerId),
                "lastMessage" to "",
                "timestamp" to System.currentTimeMillis()
            )
            chatRef.set(chatData)

            // ✅ Add chat to both users’ chatList
            db.collection("users").document(buyerId)
                .collection("chatList").document(chatId)
                .set(mapOf("chatId" to chatId))

            db.collection("users").document(sellerId)
                .collection("chatList").document(chatId)
                .set(mapOf("chatId" to chatId))
        }

        // ✅ Return chatId so you can open ChatScreen
        onChatReady(chatId)
    }
}
