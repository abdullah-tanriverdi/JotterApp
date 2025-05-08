package com.tovaxtechnology.jotter

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.tovaxtechnology.jotter.Auth.AuthViewModel
import com.tovaxtechnology.jotter.Navigation.Navigation
import com.tovaxtechnology.jotter.ui.theme.JotterTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JotterTheme {
                val navController = rememberNavController()
                Navigation(
                    navController = navController,
                    authViewModel = AuthViewModel()
                )
            }
        }
    }
}

