package com.example.authapp.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.InputStream

object CloudinaryUploader {

    private const val CLOUD_NAME = "dou7gftkd"      // Replace with your Cloudinary cloud name
    private const val UPLOAD_PRESET = "ml_default"  // Replace with your upload preset

    suspend fun uploadImage(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file", "profile.jpg",
                        RequestBody.create("image/*".toMediaTypeOrNull(), bytes!!)
                    )
                    .addFormDataPart("upload_preset", UPLOAD_PRESET)
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    json.getString("secure_url")
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
