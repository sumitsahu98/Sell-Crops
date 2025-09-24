package com.example.authapp.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.example.authapp.Screens.*
import com.example.authapp.models.CartViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    val cartViewModel: CartViewModel = viewModel()

    val startDestination = if (auth.currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        // Login Screen
        composable("login") {
            LoginScreen(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSkip = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Signup Screen
        composable("signup") {
            SignupScreen(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToLogin = { navController.navigate("login") },

                )
        }

        // Home Screen
        composable("home") {
            HomeScreen(navController = navController, cartViewModel = cartViewModel)
        }

        // Cart Screen
        composable("cart") {
            CartScreen(navController = navController, cartViewModel = cartViewModel)
        }

        // Sell Crop Screen
        composable("create") { SellCropScreen(navController) }

        // Listing Details Screen with serialized crop JSON
        composable(
            route = "details/{cropJson}",
            arguments = listOf(navArgument("cropJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val cropJson = backStackEntry.arguments?.getString("cropJson")
            ListingDetailsScreen(navController = navController, cropJson = cropJson)
        }

        // Chat, Account, Orders Screens
        composable("chat") { ChatScreen(navController) }
        composable("account") { AccountScreen(navController) }
        composable("orders") { OrdersScreen(navController) }

        // Edit Profile Screen
        composable("edit_profile") {
            EditProfileScreenModern(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
