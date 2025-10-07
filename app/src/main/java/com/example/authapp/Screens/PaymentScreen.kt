package com.example.authapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.example.authapp.repository.OrderRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    buyerName: String,
    subtotal: Int,
    buyerAddress: String,
    buyerPhone: String
) {
    val selectedItems: List<Crop> = cartViewModel.selectedForCheckout
    var isProcessing by remember { mutableStateOf(false) }
    var paymentDone by remember { mutableStateOf(false) }

    val subtotal = selectedItems.sumOf { crop ->
        val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
        val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
        priceFor10Kg * cartQty / 10
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pay Now", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Total Amount: ₹${subtotal}",
                fontSize = 22.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (paymentDone) {
                Text("Payment Successful!", color = Color(0xFF4CAF50), fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate("orders") {
                        popUpTo("checkout") { inclusive = true }
                    }
                }) {
                    Text("Go to Orders")
                }
            } else {
                Button(
                    onClick = { isProcessing = true },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isProcessing) "Processing..." else "Pay Now", color = Color.White)
                }

                if (isProcessing) {
                    LaunchedEffect(isProcessing) {
                        kotlinx.coroutines.delay(2000) // simulate payment process
                        OrderRepository.submitOrder(
                            selectedItems = selectedItems,
                            buyerName = buyerName,
                            buyerAddress = buyerAddress,
                            buyerPhone = buyerPhone,
                            onSuccess = {
                                cartViewModel.clearCart()
                                isProcessing = false
                                paymentDone = true
                            },
                            onFailure = {
                                isProcessing = false
                                // TODO: show error
                            }
                        )
                    }
                }
            }
        }
    }
}
