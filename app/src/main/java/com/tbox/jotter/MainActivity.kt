package com.tbox.jotter


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.tbox.jotter.auth.AuthViewModel
import com.tbox.jotter.navigation.AppNavigation
import com.tbox.jotter.ui.theme.JotterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            JotterTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    darkTheme = darkTheme,
                    onThemeUpdated = { darkTheme = !darkTheme },
                    authViewModel = AuthViewModel())
            }
        }
    }
}















