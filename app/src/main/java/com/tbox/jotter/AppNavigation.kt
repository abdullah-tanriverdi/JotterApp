package com.tbox.jotter

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.tbox.jotter.ScreenAssistant.ChatScreen
import com.tbox.jotter.ScreenAssistant.ChatViewModel
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesDetails
import com.tbox.jotter.auth.AuthViewModel
import com.tbox.jotter.ScreenHome.ScreenHome
import com.tbox.jotter.splash.SplashScreen
import com.tbox.jotter.login.LoginScreen
import com.tbox.jotter.login.ResetScreen
import com.tbox.jotter.ScreenProfile.ScreenProfile
import com.tbox.jotter.ScreenProfile.ScreenProfileEdit
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotes
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesAdd
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesEdit
import com.tbox.jotter.signup.SignUpScreen

@Composable
fun AppNavigation(  navController: NavHostController,
                    authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "splash") {

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






    }
}


