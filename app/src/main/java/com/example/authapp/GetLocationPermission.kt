import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun LocationPermissionButton(onGranted: () -> Unit) {
    val context = LocalContext.current

    // Launcher for runtime permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            println("Location permission denied")
        }
    }

    Button(onClick = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onGranted() // safe to access location
        } else {
            // request permission
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }) {
        Text("Use My Current Location")
    }
}
