package com.tbox.jotter.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.tbox.jotter.ScreenAssistant.ChatScreen
import com.tbox.jotter.ScreenAssistant.ChatViewModel
import com.tbox.jotter.ScreenHome.ScreenSimpleNoteAdd
import com.tbox.jotter.ScreenHome.ScreenSimpleNoteDetail
import com.tbox.jotter.auth.AuthViewModel
import com.tbox.jotter.ScreenHome.ScreenHome
import com.tbox.jotter.ScreenHome.ScreenSimpleNote
import com.tbox.jotter.ScreenHome.ScreenVideoList
import com.tbox.jotter.ScreenHome.ScreenVideoNote
import com.tbox.jotter.ScreenHome.ScreenVoiceNoteAdd
import com.tbox.jotter.ScreenHome.ScreenVoiceNoteList
import com.tbox.jotter.splash.SplashScreen
import com.tbox.jotter.login.LoginScreen
import com.tbox.jotter.login.ResetScreen
import com.tbox.jotter.ScreenProfile.ScreenProfile
import com.tbox.jotter.ScreenProfile.ScreenProfileEdit
import com.tbox.jotter.ScreenSettings.ScreenSettings
import com.tbox.jotter.signup.SignUpScreen

@Composable
fun AppNavigation(  navController: NavHostController,
                    darkTheme: Boolean,
                    onThemeUpdated: () -> Unit,
                    authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "splash") {

        val chatViewModel = ChatViewModel()
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
            ScreenHome(navController= navController)

        }

        composable("setting"){
            ScreenSettings(
                navController = navController,
                darkTheme = darkTheme, // Pass darkTheme to ScreenSettings
                onThemeUpdated = onThemeUpdated // Pass the theme toggle callback
            )       }

        composable("profile"){
            ScreenProfile(navController = navController)
        }



        composable("addNote") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenSimpleNoteAdd(navController = navController, uid = currentUser?.uid ?: "")
        }

        composable("simpleNotes") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenSimpleNote(navController = navController, uid = currentUser?.uid ?: "")
        }


        composable("noteDetail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = currentUser?.uid ?: ""
            ScreenSimpleNoteDetail(noteId = noteId, uid = uid, navController = navController)
        }


        composable("profile_screen_edit"){
            ScreenProfileEdit(navController = navController , Firebase.auth.currentUser?.uid)
        }


        composable("voiceNotes"){
            ScreenVoiceNoteAdd(navController)
        }


        composable("voiceList"){
            ScreenVoiceNoteList(navController)
        }

        composable("videoNote"){
            ScreenVideoNote(navController)
        }



        composable("videoList"){
            ScreenVideoList(navController)
        }

        composable("chat_screen"){
            ChatScreen(navController , chatViewModel)
        }



    }
}


