package com.tbox.jotter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController, isDarkTheme: Boolean, onThemeChange: () -> Unit, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            MainScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(
                innerPadding = innerPadding,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }
        composable("profile") {
            ProfileScreen( innerPadding = innerPadding)
        }
        composable("graph") {
            GraphScreen( innerPadding = innerPadding)
        }
    }
}


