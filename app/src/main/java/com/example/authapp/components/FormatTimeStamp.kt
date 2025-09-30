import java.text.SimpleDateFormat
import java.util.*

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
