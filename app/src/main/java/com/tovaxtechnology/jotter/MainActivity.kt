package com.tovaxtechnology.jotter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.tovaxtechnology.jotter.Navigation.Navigation
import com.tovaxtechnology.jotter.ui.theme.JotterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JotterTheme {
                val navController = rememberNavController()
                Navigation(
                    navController = navController
                )
            }
        }
    }
}

