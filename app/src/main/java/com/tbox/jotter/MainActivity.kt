package com.tbox.jotter


import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tbox.jotter.auth.AuthViewModel
import com.tbox.jotter.navigation.AppNavigation
import com.tbox.jotter.ui.theme.JotterTheme


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            JotterTheme() {
                val navController = rememberNavController()
               AppNavigation(navController = navController, authViewModel = AuthViewModel())


            }
        }
    }

}














