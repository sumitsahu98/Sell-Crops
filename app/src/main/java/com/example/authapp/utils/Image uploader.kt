package com.example.authapp.utils

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Reusable composable for picking an image from Gallery or Camera
 * and uploading to Firebase Storage with permissions handled.
 */
object ImagePickerUploader {

    @Composable
    fun PickAndUploadImage(
        auth: FirebaseAuth,
        onImageUploaded: (downloadUrl: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val context = LocalContext.current
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

        // Gallery launcher
        val galleryLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }

        // Camera launcher
        var cameraUri by remember { mutableStateOf<Uri?>(null) }
        val cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success && cameraUri != null) {
                selectedImageUri = cameraUri
            }
        }

        // Permission launcher
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.CAMERA] == true
            if (granted) {
                launchCamera(context) { uri -> cameraUri = uri; cameraLauncher.launch(uri) }
            } else {
                onError("Camera permission denied")
            }
        }

        // UI
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray, shape = RoundedCornerShape(50.dp))
                .clickable {
                    // For example, first open gallery; can also add dialog for camera
                    galleryLauncher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri == null) {
                Text("Add\nPhoto", color = Color.White)
            } else {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(50.dp))
                )
            }
        }

        // Upload automatically
        LaunchedEffect(selectedImageUri) {
            selectedImageUri?.let { uri ->
                uploadProfileImage(auth, uri, onImageUploaded, onError)
            }
        }
    }

    private fun launchCamera(context: Context, onUriCreated: (Uri) -> Unit) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.cacheDir, "IMG_$timeStamp.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        onUriCreated(uri)
    }

    private fun uploadProfileImage(
        auth: FirebaseAuth,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure("User not logged in")
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/$userId.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { e ->
                    onFailure(e.message ?: "Failed to get download URL")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Image upload failed")
            }
    }
}
