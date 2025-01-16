package com.tbox.jotter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tbox.jotter.login.LoginScreen
import com.tbox.jotter.login.ResetScreen
import com.tbox.jotter.signup.SignUpScreen

@Composable
fun AppNavigation(navController: NavHostController, isDarkTheme: Boolean, onThemeChange: () -> Unit, innerPadding: PaddingValues, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash"){
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("login"){
            LoginScreen(navController = navController, authViewModel = AuthViewModel())
        }

        composable("signup"){
            SignUpScreen(
                navController,authViewModel)
        }

        composable("reset"){
            ResetScreen(navController,authViewModel)
        }



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


