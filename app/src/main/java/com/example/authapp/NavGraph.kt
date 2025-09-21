//package com.example.authapp.navigation
import com.example.authapp.*

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.authapp.*
//import com.example.sumitapp.AccountScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    val cartViewModel: CartViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val startDestination = if (auth.currentUser != null) {
        "home"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {

    // 🔹 Login Screen
        composable("login") {
            LoginScreen(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 🔹 Signup Screen
        composable("signup") {
            SignupScreen(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onNavigateToHome = { navController.navigate("home") },

            )
        }

        // 🔹 Home Screen
        composable("home") {
            HomeScreen(navController = navController, cartViewModel = cartViewModel)
        }

        // 🔹 Cart Screen
        composable("cart") {
            CartScreen(navController = navController, cartViewModel = cartViewModel)
        }

        // 🔹 Other Screens
        composable("create") { SellCropScreen(navController) }
        composable("details") { ListingDetailsScreen(navController) }
        composable("chat") { ChatScreen(navController) }
        composable("account") { AccountScreen(navController) }
        composable("orders") { OrdersScreen(navController) }

        // 🔹 Edit Profile
        composable("edit_profile") {
            EditProfileScreenModern(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onBack = { navController.popBackStack() }
            )
        }
    }


}
