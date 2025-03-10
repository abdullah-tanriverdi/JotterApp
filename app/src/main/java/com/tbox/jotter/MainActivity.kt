package com.tbox.jotter


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.tbox.jotter.AppNavigation.Navigation
import com.tbox.jotter.Auth.AuthViewModel
import com.tbox.jotter.ui.theme.JotterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            JotterTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                Navigation(
                    navController = navController,
                    authViewModel = AuthViewModel())
            }
        }
    }
}















