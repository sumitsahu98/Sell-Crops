package com.example.authapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

/**
 * Fetches the user's current location and returns a detailed address.
 * Result format: "SubLocality, City (State)" if sub-locality is available,
 * otherwise "City (State)".
 *
 * Make sure location permission (ACCESS_FINE_LOCATION) is already granted
 * before calling this function.
 */
@SuppressLint("MissingPermission") // Permission is checked before calling
fun getDetailedLocation(context: Context, onLocationResult: (String) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val subLocality = addresses[0].subLocality ?: "" // e.g., Karond
                    val city = addresses[0].locality ?: ""           // e.g., Bhopal
                    val state = addresses[0].adminArea ?: ""         // e.g., MP

                    val detailedAddress = if (subLocality.isNotEmpty())
                        "$subLocality, $city"
                    else
                        "$city ($state)"

                    onLocationResult(detailedAddress)
                } else {
                    Toast.makeText(context, "Unable to fetch address", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location not available. Try moving outdoors.", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
