package com.example.authapp.navigation

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.example.authapp.Screens.*
import com.example.authapp.AccountScreens.EliteBuyerScreen
import com.example.authapp.AccountScreens.HelpSupportScreen
import com.example.authapp.AccountScreens.SettingsScreen
import com.example.authapp.AccountScreens.WishlistScreen
import com.example.authapp.models.CartViewModel
import com.example.authapp.models.Crop
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    val cartViewModel: CartViewModel = viewModel()

    val startDestination = if (auth.currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        // ✅ Login Screen
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

        // ✅ Signup Screen
        composable("signup") {
            SignupScreen(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onNavigateToEmailVerification = { email ->
                    navController.navigate("email_verification/$email")
                },
                onNavigateToLogin = { navController.navigate("login") },
            )
        }
        // ✅ Email Verification Screen
        composable("email_verification/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onVerified = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true } // optional: prevent going back to signup
                    }
                }
            )
        }

//        ✅ Home Screen
        composable("home") {
            HomeScreen(navController = navController, cartViewModel = cartViewModel)
        }

        // ✅ Cart Screen
        composable("cart") {
            CartScreen(navController = navController, cartViewModel = cartViewModel)
        }

        // ✅ Sell Crop Screen
        composable("create") { SellCropScreen(navController) }

        // ✅ My Crops (Seller) Screen
        composable("my_crops") {
            SellerCropsScreen(
                auth = auth,
                navController = navController,
                onEditCrop = { crop ->
                    val cropJson = Uri.encode(Gson().toJson(crop))
                    navController.navigate("edit_crop/$cropJson")
                }
            )
        }

        // ✅ Edit Crop Screen
        composable(
            route = "edit_crop/{cropJson}",
            arguments = listOf(navArgument("cropJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val cropJson = backStackEntry.arguments?.getString("cropJson")
            val crop: Crop? = try {
                cropJson?.let { Gson().fromJson(it, Crop::class.java) }
            } catch (e: Exception) {
                null
            }

            if (crop != null) {
                EditCropScreen(
                    crop = crop,
                    onBack = { navController.popBackStack() }
                )
            } else {
                Toast.makeText(context, "Error loading crop", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }

        // ✅ Listing Details Screen
        composable(
            route = "details/{cropJson}",
            arguments = listOf(navArgument("cropJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val cropJson = backStackEntry.arguments?.getString("cropJson")
            ListingDetailsScreen(navController = navController, cropJson = cropJson)
        }

        // ✅ Chat List Screen
        composable("chat_list") {
            ChatListScreen(navController = navController)
        }
        // ✅ Checkout Screen
        composable("checkout") {
            CheckoutScreen(navController = navController, cartViewModel = cartViewModel)
        }
// Payment Screen
        composable(
            route = "payment?subtotal={subtotal}&buyerName={buyerName}&buyerAddress={buyerAddress}&buyerPhone={buyerPhone}",
            arguments = listOf(
                navArgument("subtotal") { type = NavType.IntType },
                navArgument("buyerName") { type = NavType.StringType },
                navArgument("buyerAddress") { type = NavType.StringType },
                navArgument("buyerPhone") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subtotal = backStackEntry.arguments?.getInt("subtotal") ?: 0
            val buyerName = backStackEntry.arguments?.getString("buyerName") ?: ""
            val buyerAddress = backStackEntry.arguments?.getString("buyerAddress") ?: ""
            val buyerPhone = backStackEntry.arguments?.getString("buyerPhone") ?: ""

            PaymentScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                subtotal = subtotal,
                buyerName = buyerName,
                buyerAddress = buyerAddress,
                buyerPhone = buyerPhone
            )
        }

        // ✅ Individual Chat Screen
        composable(
            route = "chat/{sellerId}",
            arguments = listOf(
                navArgument("sellerId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val sellerId = backStackEntry.arguments?.getString("sellerId")
            ChatScreen(navController, sellerId)
        }

        // ✅ Account + Orders
        composable("account") { AccountScreen(navController) }
        composable("orders") { OrdersScreen(navController) }
        // Wishlist Screen
        composable("wishlist") {
            WishlistScreen(navController)
        }

        // Become an Elite Buyer
        composable("elite") {
            EliteBuyerScreen(navController)
        }

        // Settings
        composable("settings") {
            SettingsScreen(navController)
        }

        // Help & Support
        composable("help") {
            HelpSupportScreen(navController)
        }
        // ✅ Edit Profile Screen
        composable("edit_profile") {
            EditProfileScreenModern(
                auth = auth,
                showMessage = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
