package com.tbox.jotter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.tbox.jotter.NoteDetailScreen
import com.tbox.jotter.home.AddNoteScreen
import com.tbox.jotter.auth.AuthViewModel
import com.tbox.jotter.home.HomeScreen
import com.tbox.jotter.setting.SettingScreen
import com.tbox.jotter.home.SimpleNoteScreen
import com.tbox.jotter.graph.GraphScreen

import com.tbox.jotter.splash.SplashScreen
import com.tbox.jotter.login.LoginScreen
import com.tbox.jotter.login.ResetScreen
import com.tbox.jotter.profile.ProfileScreen
import com.tbox.jotter.signup.SignUpScreen

@Composable
fun AppNavigation(navController: NavHostController, authViewModel: AuthViewModel) {
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

        composable("home"){
            val currentUser = FirebaseAuth.getInstance().currentUser
            HomeScreen(navController= navController)

        }

        composable("setting"){
            SettingScreen(navController= navController)
        }

        composable("profile"){
            ProfileScreen(navController = navController)
        }

        composable("graph"){
            GraphScreen(navController= navController)
        }

        composable("addNote") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            AddNoteScreen(navController = navController, uid = currentUser?.uid ?: "")
        }

        composable("simpleNotes") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            SimpleNoteScreen(navController = navController, uid = currentUser?.uid ?: "")
        }


        composable("noteDetail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = currentUser?.uid ?: ""
            NoteDetailScreen(noteId = noteId, uid = uid, navController = navController)
        }




    }
}


