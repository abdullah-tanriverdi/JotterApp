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
import com.tbox.jotter.ScreenHome.ScreenVideoList
import com.tbox.jotter.ScreenHome.ScreenVideoNote
import com.tbox.jotter.ScreenHome.ScreenVoiceNoteAdd
import com.tbox.jotter.ScreenHome.ScreenVoiceNoteList
import com.tbox.jotter.splash.SplashScreen
import com.tbox.jotter.login.LoginScreen
import com.tbox.jotter.login.ResetScreen
import com.tbox.jotter.ScreenProfile.ScreenProfile
import com.tbox.jotter.ScreenProfile.ScreenProfileEdit
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotes
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesAdd
import com.tbox.jotter.ScreenQuickNotes.ScreenQuickNotesEdit
import com.tbox.jotter.ScreenSettings.ScreenSettings
import com.tbox.jotter.signup.SignUpScreen

@Composable
fun AppNavigation(  navController: NavHostController,
                    authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "splash") {


        composable("splash"){
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("home"){
            ScreenHome(navController = navController)
        }

        composable("quick_notes_add"){
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenQuickNotesAdd(navController = navController , uid = currentUser?.uid?: "")
        }

        composable("quick_notes"){
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenQuickNotes(navController = navController, uid = currentUser?.uid?: "")
        }

        composable("quick_notes_detail/{noteId}"){
                backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val currentUser = FirebaseAuth.getInstance().currentUser
            ScreenQuickNotesDetails(noteId = noteId , uid = currentUser?.uid ?: "", navController = navController)

        }

        composable("quick_notes_edit/{uid}/{noteId}"){
                backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ScreenQuickNotesEdit(navController,uid,noteId)

        }

        composable("quick_notes_chatbot"){
            val chatViewModel = ChatViewModel()
            ChatScreen(navController = navController ,chatViewModel )
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

        composable("profile"){
            ScreenProfile(navController = navController)
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


    }
}


