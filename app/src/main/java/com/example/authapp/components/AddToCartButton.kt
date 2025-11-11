package com.example.authapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun AddToCartButton(crop: Crop, cartViewModel: CartViewModel, modifier: Modifier = Modifier) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val isSeller = crop.sellerId == currentUserId   // ✅ Check if the logged-in user is the seller

    val maxUnits = ((crop.quantity.toIntOrNull() ?: 0) / 10)
    val units = remember {
        mutableStateOf(
            cartViewModel.cartItems.find { it.id == crop.id }?.cartQuantity?.toInt()?.div(10) ?: 0
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // ✅ If the seller is viewing their own crop
    if (isSeller) {
        Box(
            modifier = modifier
                .background(Color(0xFF9E9E9E), RoundedCornerShape(8.dp))
                .height(36.dp)
                .width(110.dp)
                .clickable {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("You can’t buy your own crop!")
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your Crop",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        // Snackbar display
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
        return
    }

    // ✅ If the user is a buyer
    if (units.value == 0) {
        Button(
            onClick = {
                if (maxUnits > 0) {
                    cartViewModel.addToCart(crop)
                    units.value = 1
                    cartViewModel.saveCartToFirestore()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF43A047),
                contentColor = Color.White
            ),
            modifier = modifier
                .height(36.dp)
                .width(80.dp)
        ) {
            Text("Add", fontSize = 14.sp)
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .background(Color(0xFF43A047), RoundedCornerShape(8.dp))
                .height(36.dp)
                .width(110.dp)
                .padding(horizontal = 4.dp)
        ) {
            // Decrease button
            IconButton(
                onClick = {
                    if (units.value > 1) {
                        units.value--
                        cartViewModel.decreaseCartQuantity(crop)
                    } else {
                        units.value = 0
                        cartViewModel.decreaseCartQuantity(crop)
                    }
                    cartViewModel.saveCartToFirestore()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
            }

            // Quantity display
            Text(
                units.value.toString(),
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Increase button
            IconButton(
                onClick = {
                    if (units.value < maxUnits) {
                        units.value++
                        cartViewModel.addToCart(crop)
                        cartViewModel.saveCartToFirestore()
                    }
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
            }
        }
    }
}
