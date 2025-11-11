package com.example.authapp.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.example.authapp.repository.OrderRepository
import kotlinx.coroutines.delay

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

    val totalAmount = selectedItems.sumOf { crop ->
        val priceFor10Kg = crop.price.toDoubleOrNull()?.times(10) ?: 0.0
        val cartQty = crop.cartQuantity.toIntOrNull() ?: 0
        priceFor10Kg * cartQty / 10
    }

    // 🟢 Payment method states
    var selectedMethod by remember { mutableStateOf("UPI") }
    var upiId by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total Amount: ₹${"%.2f".format(totalAmount)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🟢 Payment Method Selection
            Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val methods = listOf("UPI", "Credit/Debit Card", "Cash on Delivery")
            methods.forEach { method ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (selectedMethod == method),
                        onClick = { selectedMethod = method },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50))
                    )
                    Text(method, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🟢 Conditional Input Fields Based on Method
            when (selectedMethod) {
                "UPI" -> {
                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("Enter UPI ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                "Credit/Debit Card" -> {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Card Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cardExpiry,
                        onValueChange = { cardExpiry = it },
                        label = { Text("Expiry Date (MM/YY)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cardCvv,
                        onValueChange = { cardCvv = it },
                        label = { Text("CVV") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                "Cash on Delivery" -> {
                    Text("Pay directly at the time of delivery.", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (paymentDone) {
                Text("✅ Ordered Successful!", color = Color(0xFF4CAF50), fontSize = 20.sp)
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
                    enabled = !isProcessing && (
                            (selectedMethod == "UPI" && upiId.isNotBlank()) ||
                                    (selectedMethod == "Credit/Debit Card" &&
                                            cardNumber.length >= 12 &&
                                            cardExpiry.isNotBlank() &&
                                            cardCvv.length >= 3) ||
                                    selectedMethod == "Cash on Delivery"
                            ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isProcessing) "Processing..." else "Confirm Order", color = Color.White)
                }

                if (isProcessing) {
                    LaunchedEffect(isProcessing) {
                        delay(2000) // simulate payment delay
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
                            }
                        )
                    }
                }
            }
        }
    }
}
