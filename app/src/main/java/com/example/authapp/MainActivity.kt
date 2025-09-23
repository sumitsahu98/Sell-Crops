package com.example.authapp

//import AppNavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.authapp.navigation.AppNavGraph
//import com.example.authapp.AppNavGraph
//import com.example.authapp.navigation.AppNavGraph
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController, auth = auth)
            }
        }
    }
}
